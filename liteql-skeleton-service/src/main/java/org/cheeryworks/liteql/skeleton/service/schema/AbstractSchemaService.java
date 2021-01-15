package org.cheeryworks.liteql.skeleton.service.schema;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.Schema;
import org.cheeryworks.liteql.skeleton.schema.TraitTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.schema.annotation.LiteQLStaticType;
import org.cheeryworks.liteql.skeleton.schema.field.Field;
import org.cheeryworks.liteql.skeleton.schema.index.AbstractIndexDefinition;
import org.cheeryworks.liteql.skeleton.schema.index.IndexDefinition;
import org.cheeryworks.liteql.skeleton.schema.index.UniqueDefinition;
import org.cheeryworks.liteql.skeleton.schema.migration.Migration;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.AbstractIndexMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.CreateFieldMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.CreateIndexMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.CreateTypeMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.CreateUniqueMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.DropFieldMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.DropTypeMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.MigrationOperation;
import org.cheeryworks.liteql.skeleton.service.AbstractLiteQLService;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.cheeryworks.liteql.skeleton.schema.Schema.VERSION_BASELINE_SUFFIX;
import static org.cheeryworks.liteql.skeleton.schema.Schema.VERSION_CONCAT;

public abstract class AbstractSchemaService extends AbstractLiteQLService implements SchemaService {

    private Set<SchemaMetadata> schemaMetadataSet = new TreeSet<>(Comparator.comparing(SchemaMetadata::getName));

    private List<Schema> schemas = new ArrayList<>();

    private Map<TypeName, TypeName> traitImplements = new HashMap<>();

    private Map<TypeName, Class<?>> staticTypeMapping = new HashMap<>();

    private static final SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

    public AbstractSchemaService(LiteQLProperties liteQLProperties) {
        super(liteQLProperties);

        processStaticTypes();
    }

    private void processStaticTypes() {
        Set<Class<?>> staticTypes = new HashSet<>();

        for (String packageToScan : LiteQL.SchemaUtils.getSchemaDefinitionPackages()) {
            Reflections reflections = new Reflections(packageToScan);

            staticTypes.addAll(reflections.getTypesAnnotatedWith(LiteQLStaticType.class));
        }

        for (Class<?> staticType : staticTypes) {
            LiteQLStaticType liteQLStaticType = staticType.getAnnotation(LiteQLStaticType.class);

            staticTypeMapping.put(LiteQL.SchemaUtils.getTypeName(liteQLStaticType.value()), staticType);
        }
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
            DomainTypeDefinition domainTypeDefinition = (DomainTypeDefinition) typeDefinition;

            schema.getDomainTypeDefinitions().add(domainTypeDefinition);

            if (domainTypeDefinition.getImplementTrait() != null) {
                this.traitImplements.put(domainTypeDefinition.getImplementTrait(), domainTypeDefinition.getTypeName());
            }
        } else if (typeDefinition instanceof TraitTypeDefinition) {
            schema.getTraitTypeDefinitions().add((TraitTypeDefinition) typeDefinition);
        }

        processStaticType(typeDefinition);
    }

    private void processStaticType(TypeDefinition typeDefinition) {
        Class<?> staticType = staticTypeMapping.get(typeDefinition.getTypeName());

        if (staticType != null && typeDefinition instanceof TraitTypeDefinition) {
            ((TraitTypeDefinition) typeDefinition).getFields().stream().forEach(field -> {
                try {
                    if (FieldUtils.getField(staticType, field.getName()) != null) {
                        FieldUtils.writeStaticField(staticType, field.getName(), field, true);
                    }
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException(ex.getMessage(), ex);
                }
            });
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

            migratedDomainTypeDefinition.setVersion(domainTypeDefinition.getVersion());

            migratedDomainTypeDefinition.setTraits(domainTypeDefinition.getTraits());

            migratedDomainTypeDefinition.setImplementTrait(domainTypeDefinition.getImplementTrait());

            if (!domainTypeDefinition.isGraphQLType()) {
                migratedDomainTypeDefinition.setGraphQLType(false);
            }

            if (MapUtils.isNotEmpty(migrations)) {
                for (Migration migration : migrations.values()) {
                    for (MigrationOperation migrationOperation : migration.getOperations()) {
                        mergeMigrationOperation(migrationOperation, migratedDomainTypeDefinition);
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

    private void mergeMigrationOperation(
            MigrationOperation migrationOperation, DomainTypeDefinition domainTypeDefinition) {
        switch (migrationOperation.getType()) {
            case CREATE_TYPE:
                mergeCreateTypeMigrationOperation(
                        (CreateTypeMigrationOperation) migrationOperation, domainTypeDefinition);
                return;
            case DROP_TYPE:
                mergeDropTypeMigrationOperation(
                        (DropTypeMigrationOperation) migrationOperation, domainTypeDefinition);
                return;
            case CREATE_FIELD:
                mergeCreateFieldMigrationOperation(
                        (CreateFieldMigrationOperation) migrationOperation, domainTypeDefinition);
                return;
            case DROP_FIELD:
                mergeDropFieldMigrationOperation(
                        (DropFieldMigrationOperation) migrationOperation, domainTypeDefinition);
                return;
            case CREATE_INDEX:
            case DROP_INDEX:
            case CREATE_UNIQUE:
            case DROP_UNIQUE:
                mergeIndexMigrationOperation(
                        (AbstractIndexMigrationOperation<? extends AbstractIndexDefinition>) migrationOperation,
                        domainTypeDefinition);
                return;
            default:
                return;
        }
    }

    private void mergeCreateTypeMigrationOperation(
            CreateTypeMigrationOperation createTypeMigrationOperation, DomainTypeDefinition domainTypeDefinition) {
        CreateFieldMigrationOperation createFieldMigrationOperation
                = new CreateFieldMigrationOperation(createTypeMigrationOperation.getFields());
        mergeCreateFieldMigrationOperation(createFieldMigrationOperation, domainTypeDefinition);

        if (CollectionUtils.isNotEmpty(createTypeMigrationOperation.getUniques())) {
            CreateUniqueMigrationOperation createUniqueMigrationOperation
                    = new CreateUniqueMigrationOperation(createTypeMigrationOperation.getUniques());
            mergeIndexMigrationOperation(createUniqueMigrationOperation, domainTypeDefinition);
        }

        if (CollectionUtils.isNotEmpty(createTypeMigrationOperation.getIndexes())) {
            CreateIndexMigrationOperation createIndexMigrationOperation
                    = new CreateIndexMigrationOperation(createTypeMigrationOperation.getIndexes());
            mergeIndexMigrationOperation(createIndexMigrationOperation, domainTypeDefinition);
        }
    }

    private void mergeDropTypeMigrationOperation(
            DropTypeMigrationOperation dropTypeMigrationOperation, DomainTypeDefinition domainTypeDefinition) {
        domainTypeDefinition.setDropped(true);
    }

    private void mergeCreateFieldMigrationOperation(
            CreateFieldMigrationOperation createFieldMigrationOperation, DomainTypeDefinition domainTypeDefinition) {
        if (CollectionUtils.isEmpty(domainTypeDefinition.getFields())) {
            domainTypeDefinition.setFields(new HashSet<>());
        }

        domainTypeDefinition.getFields().addAll(createFieldMigrationOperation.getFields());
    }

    private void mergeDropFieldMigrationOperation(
            DropFieldMigrationOperation dropFieldMigrationOperation, DomainTypeDefinition domainTypeDefinition) {
        for (String field : dropFieldMigrationOperation.getFields()) {
            Iterator<Field> fieldIterator = domainTypeDefinition.getFields().iterator();

            while (fieldIterator.hasNext()) {
                if (fieldIterator.next().getName().equalsIgnoreCase(field)) {
                    fieldIterator.remove();
                }
            }
        }
    }

    private void mergeIndexMigrationOperation(
            AbstractIndexMigrationOperation<? extends AbstractIndexDefinition> indexMigrationOperation,
            DomainTypeDefinition domainTypeDefinition) {
        switch (indexMigrationOperation.getType()) {
            case CREATE_UNIQUE:
                for (AbstractIndexDefinition index : indexMigrationOperation.getIndexes()) {
                    if (CollectionUtils.isEmpty(domainTypeDefinition.getUniques())) {
                        domainTypeDefinition.setUniques(new HashSet<>());
                    }

                    domainTypeDefinition.getUniques().add((UniqueDefinition) index);
                }

                return;
            case CREATE_INDEX:
                for (AbstractIndexDefinition index : indexMigrationOperation.getIndexes()) {
                    if (CollectionUtils.isEmpty(domainTypeDefinition.getIndexes())) {
                        domainTypeDefinition.setIndexes(new HashSet<>());
                    }

                    domainTypeDefinition.getIndexes().add((IndexDefinition) index);
                }

                return;
            case DROP_UNIQUE:
                dropIndex(indexMigrationOperation.getIndexes(), domainTypeDefinition.getUniques());

                return;
            case DROP_INDEX:
                dropIndex(indexMigrationOperation.getIndexes(), domainTypeDefinition.getIndexes());

                return;
            default:
                return;
        }
    }

    private void dropIndex(
            Set<? extends AbstractIndexDefinition> indexes, Set<? extends AbstractIndexDefinition> existIndexes) {
        for (AbstractIndexDefinition index : indexes) {
            Iterator<? extends AbstractIndexDefinition> existIndexIterator = existIndexes.iterator();

            while (existIndexIterator.hasNext()) {
                if (existIndexIterator.next().equals(index)) {
                    existIndexIterator.remove();
                }
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
    public TypeName getTraitImplement(TypeName traitTypeName) {
        return this.traitImplements.get(traitTypeName);
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

    protected static class SchemaMetadata {

        private String name;

        private Map<String, TypeMetadata> typeMetadataSet = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        public String getName() {
            return name;
        }

        public Map<String, TypeMetadata> getTypeMetadataSet() {
            return typeMetadataSet;
        }

        public SchemaMetadata(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof SchemaMetadata)) {
                return false;
            }

            SchemaMetadata that = (SchemaMetadata) o;

            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

    }

    protected static class TypeMetadata {

        private String name;

        private Map<String, String> contents = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        private Map<String, String> migrationContents = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        public String getName() {
            return name;
        }

        public Map<String, String> getContents() {
            return contents;
        }

        public void setContents(Map<String, String> contents) {
            this.contents = contents;
        }

        public Map<String, String> getMigrationContents() {
            return migrationContents;
        }

        public TypeMetadata(String name) {
            this.name = name;
        }

    }

}
