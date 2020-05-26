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
import java.util.TreeMap;

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
                path + (path.endsWith("/") ? "" : "/") + NAME_OF_TYPES_DIRECTORY);

        for (Map.Entry<String, String> typeJsonFileEntry : typeJsonFiles.entrySet()) {
            String typePath = typeJsonFileEntry.getKey();
            String[] definitionInformation = typePath.split("/");
            String schema = definitionInformation[0];
            String domainTypeName = definitionInformation[0] + "." + definitionInformation[1];

            if (typePath.endsWith(NAME_OF_TYPE_DEFINITION)) {
                DomainType domainType = LiteQLJsonUtil.toBean(typeJsonFileEntry.getValue(), DomainType.class);

                domainType.setSchema(schema);
                domainType.setName(domainTypeName);

                addType(schema, domainTypeName, domainType);
            }

            if (typePath.contains("/" + NAME_OF_MIGRATIONS_DIRECTORY + "/")) {
                String migrationName = definitionInformation[3].substring(
                        0, definitionInformation[3].indexOf(SUFFIX_OF_CONFIGURATION_FILE));
                Migration migration = LiteQLJsonUtil.toBean(typeJsonFileEntry.getValue(), Migration.class);
                migration.setName(migrationName);
                migration.setSchema(schema);
                migration.setDomainType(domainTypeName);

                addMigration(schema, migrationName, migration);
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
            migrationsInSchema = new TreeMap<>(String::compareToIgnoreCase);
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
    public DomainType getDomainType(String domainTypeName) {
        return schemas.get(domainTypeName.split("\\.")[0]).get(domainTypeName);
    }

    @Override
    public List<Migration> getMigrations(String schemaName) {
        Collection<Migration> migrationsInSchema = migrations.get(schemaName).values();

        return Arrays.asList(migrationsInSchema.toArray(new Migration[migrationsInSchema.size()]));
    }

}
