package org.cheeryworks.liteql.service.schema;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.schema.Schema;
import org.cheeryworks.liteql.schema.TraitTypeDefinition;
import org.cheeryworks.liteql.schema.TypeDefinition;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.migration.Migration;
import org.cheeryworks.liteql.schema.migration.operation.MigrationOperation;
import org.cheeryworks.liteql.service.AbstractLiteQLService;
import org.cheeryworks.liteql.util.LiteQL;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.cheeryworks.liteql.service.schema.SchemaMetadata.VERSION_BASELINE_SUFFIX;
import static org.cheeryworks.liteql.service.schema.SchemaMetadata.VERSION_CONCAT;

public abstract class AbstractSchemaService extends AbstractLiteQLService implements SchemaService {

    private Set<SchemaMetadata> schemaMetadataSet = new TreeSet<>(Comparator.comparing(SchemaMetadata::getName));

    private List<Schema> schemas = new ArrayList<>();

    private static final SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

    public AbstractSchemaService(LiteQLProperties liteQLProperties) {
        super(liteQLProperties);
    }

    protected void processSchemaMetadata(SchemaMetadata schemaMetadata) {
        if (!this.schemaMetadataSet.contains(schemaMetadata)) {
            this.schemaMetadataSet.add(schemaMetadata);
        } else {
            throw new IllegalArgumentException("Schema [" + schemaMetadata.getName() + "] exist");
        }

        for (TypeMetadata typeMetadata : schemaMetadata.getTypeMetadataSet().values()) {
            TypeName typeName = new TypeName(schemaMetadata.getName(), typeMetadata.getName());

            String key = typeMetadata
                    .getContents()
                    .keySet()
                    .stream()
                    .max(String::compareToIgnoreCase)
                    .get();

            TypeDefinition typeDefinition = LiteQL.JacksonJsonUtils.toBean(
                    typeMetadata.getContents().get(key), TypeDefinition.class);

            TraitTypeDefinition traitTypeDefinition = (TraitTypeDefinition) typeDefinition;
            traitTypeDefinition.setTypeName(typeName);
            traitTypeDefinition.setVersion(key.split(VERSION_CONCAT)[0]);

            addType(typeDefinition);

            for (Map.Entry<String, String> migrationContent : typeMetadata.getMigrationContents().entrySet()) {
                if (!migrationContent.getKey().startsWith(key.substring(0, key.indexOf(VERSION_CONCAT)))) {
                    continue;
                }

                Migration migration = LiteQL.JacksonJsonUtils.toBean(migrationContent.getValue(), Migration.class);

                migration.setName(migrationContent.getKey());
                migration.setVersion(migrationContent.getKey().split(VERSION_CONCAT)[0]);
                migration.setBaseline(migration.getVersion().endsWith(VERSION_BASELINE_SUFFIX));
                migration.setDescription(migrationContent.getKey().split(VERSION_CONCAT)[1]);

                migration.setDomainTypeName(typeName);

                addMigration(migration);
            }
        }

        verifyMigrationsOfSchema(schemaMetadata.getName());
    }

    protected TypeDefinition getType(TypeName typeName) {
        TypeDefinition typeDefinition = getDomainTypeDefinition(typeName);

        if (typeDefinition == null) {
            typeDefinition = getTraitTypeDefinition(typeName);
        }

        return typeDefinition;
    }

    protected void addType(TypeDefinition typeDefinition) {
        Schema schema = getSchema(typeDefinition.getTypeName().getSchema());

        if (schema == null) {
            schema = new Schema(typeDefinition.getTypeName().getSchema());
            this.schemas.add(schema);
        }

        if (typeDefinition instanceof DomainTypeDefinition) {
            schema.getDomainTypeDefinitions().add((DomainTypeDefinition) typeDefinition);
        } else if (typeDefinition instanceof TraitTypeDefinition) {
            schema.getTraitTypeDefinitions().add((TraitTypeDefinition) typeDefinition);
        }
    }

    private void addMigration(Migration migration) {
        Schema schema = getSchema(migration.getDomainTypeName().getSchema());

        Map<TypeName, Map<String, Migration>> migrationsOfSchema = schema.getMigrations();

        Map<String, Migration> migrations = migrationsOfSchema.get(migration.getDomainTypeName());

        if (migrations == null) {
            migrations = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

            migrationsOfSchema.put(migration.getDomainTypeName(), migrations);
        }

        migrations.put(migration.getName(), migration);
    }

    private void verifyMigrationsOfSchema(String schemaName) {
        Schema schema = getSchema(schemaName);

        for (DomainTypeDefinition domainTypeDefinition : schema.getDomainTypeDefinitions()) {
            Map<String, Migration> migrations = schema.getMigrations().get(domainTypeDefinition.getTypeName());

            DomainTypeDefinition migratedDomainTypeDefinition =
                    new DomainTypeDefinition(domainTypeDefinition.getTypeName());

            migratedDomainTypeDefinition.setTraits(domainTypeDefinition.getTraits());

            if (!domainTypeDefinition.isGraphQLType()) {
                migratedDomainTypeDefinition.setGraphQLType(false);
            }

            if (MapUtils.isNotEmpty(migrations)) {
                for (Migration migration : migrations.values()) {
                    for (MigrationOperation migrationOperation : migration.getOperations()) {
                        migrationOperation.merge(migratedDomainTypeDefinition);
                    }
                }
            }

            if (!domainTypeDefinition.equals(migratedDomainTypeDefinition)) {
                throw new IllegalStateException(
                        "Migrations of domain type [" + domainTypeDefinition.getTypeName().getFullname() + "]"
                                + " is not matched"
                                + ", definition is " + LiteQL.JacksonJsonUtils.toJson(domainTypeDefinition)
                                + ", but migrated is " + LiteQL.JacksonJsonUtils.toJson(migratedDomainTypeDefinition));
            }
        }

        for (TypeName domainTypeName : schema.getMigrations().keySet()) {
            DomainTypeDefinition domainTypeDefinition = getDomainTypeDefinition(domainTypeName);

            if (domainTypeDefinition == null) {
                throw new IllegalStateException(
                        "Migrations of domain type [" + domainTypeName.getFullname() + "] is not matched"
                                + ", definition is missed");
            }
        }
    }

    @Override
    public Set<String> getSchemaNames() {
        Set<String> schemaNames = this.schemas
                .stream()
                .map(schema -> schema.getName())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return schemaNames;
    }

    @Override
    public Set<TraitTypeDefinition> getTraitTypeDefinitions(String schemaName) {
        Schema schema = getSchema(schemaName);

        if (schema != null) {
            return schema.getTraitTypeDefinitions();
        }

        return Collections.EMPTY_SET;
    }

    @Override
    public Set<DomainTypeDefinition> getDomainTypeDefinitions(String schemaName) {
        Schema schema = getSchema(schemaName);

        if (schema != null) {
            return schema.getDomainTypeDefinitions();
        }

        return Collections.EMPTY_SET;
    }

    private Schema getSchema(String schemaName) {
        return this.schemas
                .stream()
                .filter(schema -> schema.getName().equalsIgnoreCase(schemaName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public TraitTypeDefinition getTraitTypeDefinition(TypeName typeName) {
        Objects.requireNonNull(typeName, "TypeName is required");

        Schema schema = getSchema(typeName.getSchema());

        if (schema != null && schema.getTraitTypeDefinitions() != null) {
            return getSchema(typeName.getSchema())
                    .getTraitTypeDefinitions()
                    .stream()
                    .filter(traitType -> traitType.getTypeName().getName().equalsIgnoreCase(typeName.getName()))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    @Override
    public DomainTypeDefinition getDomainTypeDefinition(TypeName typeName) {
        Objects.requireNonNull(typeName, "TypeName is required");

        Schema schema = getSchema(typeName.getSchema());

        if (schema != null && schema.getDomainTypeDefinitions() != null) {
            return getSchema(typeName.getSchema())
                    .getDomainTypeDefinitions()
                    .stream()
                    .filter(domainType -> domainType.getTypeName().getName().equalsIgnoreCase(typeName.getName()))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    @Override
    public Map<TypeName, Map<String, Migration>> getMigrations(String schemaName) {
        Schema schema = getSchema(schemaName);

        if (schema != null) {
            return schema.getMigrations();
        }

        return null;
    }

    @Override
    public String export() {
        String schemasRootPath
                = getLiteQLProperties().getDataPath() + "/schemas-" + FILE_NAME_FORMAT.format(new Date());

        try {
            File schemasRoot = new File(schemasRootPath);

            if (schemasRoot.exists()) {
                FileUtils.deleteDirectory(schemasRoot);
            } else {
                schemasRoot.mkdir();
            }

            for (Schema schema : schemas) {
                File liteQLSchema = new File(
                        schemasRoot.getPath() + "/" + schema.getName() + Schema.SUFFIX_OF_SCHEMA_ROOT_FILE);

                FileUtils.write(liteQLSchema, "", StandardCharsets.UTF_8);

                File typesDirectory = new File(
                        schemasRoot.getPath() + "/" + schema.getName() + "/" + Schema.NAME_OF_TYPES_DIRECTORY);

                typesDirectory.mkdirs();

                exportTypes(typesDirectory.getPath(), schema.getDomainTypeDefinitions());

                exportTypes(typesDirectory.getPath(), schema.getTraitTypeDefinitions());
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }

        return schemasRootPath;
    }

    private void exportTypes(
            String typesDirectoryPath, Set<? extends TypeDefinition> typeDefinitions) throws IOException {
        for (TypeDefinition typeDefinition : typeDefinitions) {
            File typeDirectory = new File(typesDirectoryPath + "/" + typeDefinition.getTypeName().getName());

            typeDirectory.mkdir();

            File typeDefinitionFile = new File(
                    typeDirectory + "/" + typeDefinition.getVersion()
                            + VERSION_CONCAT + Schema.SUFFIX_OF_TYPE_DEFINITION);

            FileUtils.write(
                    typeDefinitionFile,
                    LiteQL.JacksonJsonUtils.toJson(typeDefinition) + "\n",
                    StandardCharsets.UTF_8);

            if (!typeDefinition.isTrait()) {
                DomainTypeDefinition domainTypeDefinition = (DomainTypeDefinition) typeDefinition;

                Map<String, Migration> migrations =
                        getMigrations(domainTypeDefinition.getTypeName().getSchema())
                                .get(domainTypeDefinition.getTypeName());

                exportMigrations(domainTypeDefinition, migrations, typeDirectory);
            }
        }
    }

    protected void exportMigrations(
            DomainTypeDefinition domainTypeDefinition,
            Map<String, Migration> migrations, File typeDirectory) throws IOException {
        if (MapUtils.isNotEmpty(migrations)) {
            for (Map.Entry<String, Migration> migrationEntry : migrations.entrySet()) {
                File migrationDefinitionFile = new File(
                        typeDirectory + "/" + Schema.NAME_OF_MIGRATIONS_DIRECTORY + "/"
                                + migrationEntry.getKey() + Schema.SUFFIX_OF_CONFIGURATION_FILE);

                FileUtils.write(
                        migrationDefinitionFile,
                        LiteQL.JacksonJsonUtils.toJson(migrationEntry.getValue()) + "\n",
                        StandardCharsets.UTF_8);
            }
        }
    }

}
