package org.cheeryworks.liteql.service.repository;

import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.migration.Migration;
import org.cheeryworks.liteql.model.util.json.JsonReader;
import org.cheeryworks.liteql.model.util.json.LiteQLJsonUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileSystemRepository implements Repository {

    private String path;

    private Map<String, Map<String, DomainType>> schemas = new LinkedHashMap<>();

    private Map<String, Map<String, Migration>> migrations = new LinkedHashMap<>();

    public FileSystemRepository(String path) {
        this.path = path;

        init();
    }

    private void init() {
        Map<String, String> typeJsonFiles = JsonReader.readJsonFiles(
                path + (path.endsWith("/") ? "" : "/") + "types");

        for (Map.Entry<String, String> typeJsonFileEntry : typeJsonFiles.entrySet()) {
            String typePath = typeJsonFileEntry.getKey();
            String[] definitionInformation = typePath.split("/");
            String schemaName = definitionInformation[0];
            String typeName = definitionInformation[1];

            if (typePath.endsWith("definition.json")) {
                DomainType domainType = LiteQLJsonUtil.toBean(typeJsonFileEntry.getValue(), DomainType.class);

                domainType.setSchema(schemaName);
                domainType.setName(typeName);

                addType(schemaName, typeName, domainType);
            }

            if (typePath.contains("/migrations/")) {
                String migrationName = definitionInformation[3].substring(
                        0, definitionInformation[3].indexOf(".json"));
                Migration migration = LiteQLJsonUtil.toBean(typeJsonFileEntry.getValue(), Migration.class);
                migration.setName(migrationName);
                migration.setSchema(schemaName);
                migration.setDomainType(typeName);

                addMigration(schemaName, migrationName, migration);
            }
        }
    }

    private void addType(String schemaName, String typeName, DomainType type) {
        Map<String, DomainType> schema = schemas.get(schemaName);

        if (schema == null) {
            schema = new LinkedHashMap<>();
            schemas.put(schemaName, schema);
        }

        schema.put(typeName, type);
    }

    private void addMigration(String schemaName, String migrationName, Migration migration) {
        Map<String, Migration> migrationsInSchema = migrations.get(schemaName);

        if (migrationsInSchema == null) {
            migrationsInSchema = new LinkedHashMap<>();
            migrations.put(schemaName, migrationsInSchema);
        }

        migrationsInSchema.put(migrationName, migration);
    }

    @Override
    public Set<String> getSchemas() {
        return schemas.keySet();
    }

    @Override
    public Map<String, DomainType> getDomainTypes(String schemaName) {
        return schemas.get(schemaName);
    }

    @Override
    public DomainType getDomainType(String schemaName, String domainTypeName) {
        return schemas.get(schemaName).get(domainTypeName);
    }

    @Override
    public List<Migration> getMigrations(String schemaName) {
        Collection<Migration> migrationsInSchema = migrations.get(schemaName).values();

        return Arrays.asList(migrationsInSchema.toArray(new Migration[migrationsInSchema.size()]));
    }

}
