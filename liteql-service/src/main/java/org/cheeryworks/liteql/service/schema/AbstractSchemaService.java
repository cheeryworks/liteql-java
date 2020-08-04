package org.cheeryworks.liteql.service.schema;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.Schema;
import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.Type;
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

import static org.cheeryworks.liteql.service.schema.SchemaDefinition.VERSION_BASELINE_SUFFIX;
import static org.cheeryworks.liteql.service.schema.SchemaDefinition.VERSION_CONCAT;

public abstract class AbstractSchemaService extends AbstractLiteQLService implements SchemaService {

    private Set<SchemaDefinition> schemaDefinitions = new TreeSet<>(Comparator.comparing(SchemaDefinition::getName));

    private List<Schema> schemas = new ArrayList<>();

    private static final SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

    public AbstractSchemaService(LiteQLProperties liteQLProperties) {
        super(liteQLProperties);
    }

    protected void processSchemaDefinition(SchemaDefinition schemaDefinition) {
        if (!this.schemaDefinitions.contains(schemaDefinition)) {
            this.schemaDefinitions.add(schemaDefinition);
        } else {
            throw new IllegalArgumentException("Schema [" + schemaDefinition.getName() + "] exist");
        }

        for (TypeDefinition typeDefinition : schemaDefinition.getTypeDefinitions().values()) {
            TypeName typeName = new TypeName(schemaDefinition.getName(), typeDefinition.getName());

            String key = typeDefinition
                    .getContents()
                    .keySet()
                    .stream()
                    .max(String::compareToIgnoreCase)
                    .get();

            Type type = LiteQL.JacksonJsonUtils.toBean(typeDefinition.getContents().get(key), Type.class);

            TraitType traitType = (TraitType) type;
            traitType.setTypeName(typeName);
            traitType.setVersion(key.split(VERSION_CONCAT)[0]);

            addType(type);

            for (Map.Entry<String, String> migrationContent : typeDefinition.getMigrationContents().entrySet()) {
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

        verifyMigrationsOfSchema(schemaDefinition.getName());
    }

    protected Type getType(TypeName typeName) {
        Type type = getDomainType(typeName);

        if (type == null) {
            type = getTraitType(typeName);
        }

        return type;
    }

    protected void addType(Type type) {
        Schema schema = getSchema(type.getTypeName().getSchema());

        if (schema == null) {
            schema = new Schema(type.getTypeName().getSchema());
            this.schemas.add(schema);
        }

        if (type instanceof DomainType) {
            schema.getDomainTypes().add((DomainType) type);
        } else if (type instanceof TraitType) {
            schema.getTraitTypes().add((TraitType) type);
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

        for (DomainType domainType : schema.getDomainTypes()) {
            Map<String, Migration> migrations = schema.getMigrations().get(domainType.getTypeName());

            DomainType migratedDomainType = new DomainType(domainType.getTypeName());

            migratedDomainType.setTraits(domainType.getTraits());

            if (!domainType.isGraphQLType()) {
                migratedDomainType.setGraphQLType(false);
            }

            if (MapUtils.isNotEmpty(migrations)) {
                for (Migration migration : migrations.values()) {
                    for (MigrationOperation migrationOperation : migration.getOperations()) {
                        migrationOperation.merge(migratedDomainType);
                    }
                }
            }

            if (!domainType.equals(migratedDomainType)) {
                throw new IllegalStateException(
                        "Migrations of domain type [" + domainType.getTypeName().getFullname() + "] is not matched"
                                + ", definition is " + LiteQL.JacksonJsonUtils.toJson(domainType)
                                + ", but migrated is " + LiteQL.JacksonJsonUtils.toJson(migratedDomainType));
            }
        }

        for (TypeName domainTypeName : schema.getMigrations().keySet()) {
            DomainType domainType = getDomainType(domainTypeName);

            if (domainType == null) {
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
    public Set<TraitType> getTraitTypes(String schemaName) {
        Schema schema = getSchema(schemaName);

        if (schema != null) {
            return schema.getTraitTypes();
        }

        return Collections.EMPTY_SET;
    }

    @Override
    public Set<DomainType> getDomainTypes(String schemaName) {
        Schema schema = getSchema(schemaName);

        if (schema != null) {
            return schema.getDomainTypes();
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
    public TraitType getTraitType(TypeName typeName) {
        Objects.requireNonNull(typeName, "TypeName is required");

        Schema schema = getSchema(typeName.getSchema());

        if (schema != null && schema.getTraitTypes() != null) {
            return getSchema(typeName.getSchema())
                    .getTraitTypes()
                    .stream()
                    .filter(traitType -> traitType.getTypeName().getName().equalsIgnoreCase(typeName.getName()))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    @Override
    public DomainType getDomainType(TypeName typeName) {
        Objects.requireNonNull(typeName, "TypeName is required");

        Schema schema = getSchema(typeName.getSchema());

        if (schema != null && schema.getDomainTypes() != null) {
            return getSchema(typeName.getSchema())
                    .getDomainTypes()
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

                exportTypes(typesDirectory.getPath(), schema.getDomainTypes());

                exportTypes(typesDirectory.getPath(), schema.getTraitTypes());
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }

        return schemasRootPath;
    }

    private void exportTypes(String typesDirectoryPath, Set<? extends Type> types) throws IOException {
        for (Type type : types) {
            File typeDirectory = new File(typesDirectoryPath + "/" + type.getTypeName().getName());

            typeDirectory.mkdir();

            File typeDefinition = new File(
                    typeDirectory + "/" + type.getVersion() + VERSION_CONCAT + Schema.SUFFIX_OF_TYPE_DEFINITION);

            FileUtils.write(typeDefinition, LiteQL.JacksonJsonUtils.toJson(type) + "\n", StandardCharsets.UTF_8);

            if (!type.isTrait()) {
                DomainType domainType = (DomainType) type;

                Map<String, Migration> migrations
                        = getMigrations(domainType.getTypeName().getSchema()).get(domainType.getTypeName());

                exportMigrations(domainType, migrations, typeDirectory);
            }
        }
    }

    protected void exportMigrations(
            DomainType domainType, Map<String, Migration> migrations, File typeDirectory) throws IOException {
        if (MapUtils.isNotEmpty(migrations)) {
            for (Map.Entry<String, Migration> migrationEntry : migrations.entrySet()) {
                File migrationDefinitionFile = new File(
                        typeDirectory + "/" + Schema.NAME_OF_MIGRATIONS_DIRECTORY + "/"
                                + migrationEntry.getKey() + Schema.SUFFIX_OF_CONFIGURATION_FILE);

                FileUtils.write(
                        migrationDefinitionFile, LiteQL.JacksonJsonUtils.toJson(migrationEntry.getValue()) + "\n",
                        StandardCharsets.UTF_8);
            }
        }
    }

}
