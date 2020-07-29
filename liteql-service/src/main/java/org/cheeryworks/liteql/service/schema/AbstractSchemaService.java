package org.cheeryworks.liteql.service.schema;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.Schema;
import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.Type;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.migration.Migration;
import org.cheeryworks.liteql.schema.migration.operation.MigrationOperation;
import org.cheeryworks.liteql.service.AbstractLiteQLService;
import org.cheeryworks.liteql.util.LiteQLUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.cheeryworks.liteql.service.schema.SchemaDefinition.VERSION_BASELINE_SUFFIX;
import static org.cheeryworks.liteql.service.schema.SchemaDefinition.VERSION_CONCAT;

public abstract class AbstractSchemaService extends AbstractLiteQLService implements SchemaService {

    private Set<SchemaDefinition> schemaDefinitions = new TreeSet<>(Comparator.comparing(SchemaDefinition::getName));

    private List<Schema> schemas = new ArrayList<>();

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

            Type type = LiteQLUtil.toBean(typeDefinition.getContents().get(key), Type.class);

            type.setTypeName(typeName);

            addType(type);

            for (Map.Entry<String, String> migrationContent : typeDefinition.getMigrationContents().entrySet()) {
                if (!migrationContent.getKey().startsWith(key.substring(0, key.indexOf(VERSION_CONCAT)))) {
                    continue;
                }

                Migration migration = LiteQLUtil.toBean(migrationContent.getValue(), Migration.class);

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

    public void addType(Type type) {
        Schema schema = null;

        try {
            schema = getSchema(type.getTypeName().getSchema());
        } catch (Exception ex) {
        }

        if (schema == null) {
            schema = new Schema(type.getTypeName().getSchema());
            this.schemas.add(schema);
        }

        if (type instanceof DomainType) {
            Set<DomainType> domainTypes = schema.getDomainTypes();

            domainTypes.add((DomainType) type);
        } else if (type instanceof TraitType) {
            Set<TraitType> traitTypes = schema.getTraitTypes();

            traitTypes.add((TraitType) type);
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

            for (Migration migration : migrations.values()) {
                for (MigrationOperation migrationOperation : migration.getOperations()) {
                    migrationOperation.merge(migratedDomainType);
                }
            }

            if (!domainType.equals(migratedDomainType)) {
                throw new IllegalStateException(
                        "Migrations of domain type [" + domainType.getTypeName().getFullname() + "] is not matched"
                                + ", definition is " + LiteQLUtil.toJson(domainType)
                                + ", but migrated is " + LiteQLUtil.toJson(migratedDomainType));
            }
        }

        for (TypeName domainTypeName : schema.getMigrations().keySet()) {
            try {
                getDomainType(domainTypeName);
            } catch (Exception ex) {
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
        return Optional.ofNullable(getSchema(schemaName).getTraitTypes()).orElse(Collections.EMPTY_SET);
    }

    @Override
    public Set<DomainType> getDomainTypes(String schemaName) {
        return Optional.ofNullable(getSchema(schemaName).getDomainTypes()).orElse(Collections.EMPTY_SET);
    }

    private Schema getSchema(String schemaName) {
        return this.schemas
                .stream()
                .filter(schema -> schema.getName().equalsIgnoreCase(schemaName))
                .findFirst()
                .<IllegalStateException>orElseThrow(() -> {
                    throw new IllegalStateException("Can not get schema [" + schemaName + "]");
                });
    }

    @Override
    public TraitType getTraitType(TypeName typeName) {
        Objects.requireNonNull(typeName, "TypeName is required");

        return getSchema(typeName.getSchema())
                .getTraitTypes()
                .stream()
                .filter(traitType -> traitType.getTypeName().getName().equalsIgnoreCase(typeName.getName()))
                .findFirst()
                .<IllegalStateException>orElseThrow(() -> {
                    throw new IllegalStateException("Can not get trait typeName [" + typeName.getFullname() + "]");
                });
    }

    @Override
    public DomainType getDomainType(TypeName typeName) {
        Objects.requireNonNull(typeName, "TypeName is required");

        return getSchema(typeName.getSchema())
                .getDomainTypes()
                .stream()
                .filter(domainType -> domainType.getTypeName().getName().equalsIgnoreCase(typeName.getName()))
                .findFirst()
                .<IllegalStateException>orElseThrow(() -> {
                    throw new IllegalStateException("Can not get domain typeName [" + typeName.getFullname() + "]");
                });
    }

    @Override
    public Map<TypeName, Map<String, Migration>> getMigrations(String schemaName) {
        return getSchema(schemaName).getMigrations();
    }

}
