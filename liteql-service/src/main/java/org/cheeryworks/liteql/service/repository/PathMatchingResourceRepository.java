package org.cheeryworks.liteql.service.repository;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.migration.Migration;
import org.cheeryworks.liteql.model.util.json.LiteQLJsonUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PathMatchingResourceRepository implements Repository {

    private String[] locationPatterns;

    private Map<String, Map<String, DomainType>> schemas = new LinkedHashMap<>();

    private Map<String, Map<String, Migration>> migrations = new LinkedHashMap<>();

    public PathMatchingResourceRepository(String... locationPatterns) {
        this.locationPatterns = locationPatterns;

        init();
    }

    private void init() {

        for (String locationPattern : locationPatterns) {
            try {
                locationPattern = StringUtils.removeEnd(
                        org.springframework.util.StringUtils.cleanPath(locationPattern), "/");

                PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver
                        = new PathMatchingResourcePatternResolver(getClass().getClassLoader());

                Resource[] schemaRootResources
                        = pathMatchingResourcePatternResolver.getResources(locationPattern + "/*.yml");

                Map<String, Map<String, String>> schemaDefinitions = new HashMap<>();

                Map<String, String> schemaPaths = new HashMap<>();

                for (Resource schemaRootResource : schemaRootResources) {
                    String schemaName = schemaRootResource.getFilename()
                            .substring(0, schemaRootResource.getFilename().lastIndexOf("."));
                    String schemaRootResourcePath = schemaRootResource.getURL().getPath()
                            .substring(0, schemaRootResource.getURL().getPath().lastIndexOf("."));

                    schemaDefinitions.put(schemaName, new HashMap<>());
                    schemaPaths.put(schemaName, schemaRootResourcePath);
                }

                Resource[] schemaDefinitionResources
                        = pathMatchingResourcePatternResolver.getResources(locationPattern + "/**/*.json");

                for (Resource schemaDefinitionResource : schemaDefinitionResources) {
                    String schemaDefinitionResourcePath = schemaDefinitionResource.getURL().getPath();

                    for (Map.Entry<String, String> schemaPathEntry : schemaPaths.entrySet()) {
                        if (schemaDefinitionResourcePath.startsWith(schemaPathEntry.getValue())) {
                            String schemaDefinitionResourceRelativePath
                                    = schemaDefinitionResourcePath.substring(schemaPathEntry.getValue().length() + 1);
                            schemaDefinitions.get(schemaPathEntry.getKey()).put(
                                    schemaDefinitionResourceRelativePath,
                                    IOUtils.toString(
                                            schemaDefinitionResource.getInputStream(), StandardCharsets.UTF_8));
                            break;
                        }
                    }
                }

                for (Map.Entry<String, Map<String, String>> schemaDefinition : schemaDefinitions.entrySet()) {
                    String schemaName = schemaDefinition.getKey();

                    Map<String, String> schemaDefinitionContents = schemaDefinition.getValue();

                    for (Map.Entry<String, String> schemaDefinitionContent : schemaDefinitionContents.entrySet()) {
                        String schemaDefinitionResourceRelativePath = schemaDefinitionContent.getKey();

                        if (!schemaDefinitionResourceRelativePath.contains(Repository.NAME_OF_TYPES_DIRECTORY)) {
                            continue;
                        }

                        String domainTypeName = schemaName + "." + schemaDefinitionResourceRelativePath.split("/")[1];

                        if (schemaDefinitionResourceRelativePath.endsWith(NAME_OF_TYPE_DEFINITION)) {
                            DomainType domainType = LiteQLJsonUtil.toBean(
                                    schemaDefinitionContent.getValue(), DomainType.class);

                            domainType.setSchema(schemaName);
                            domainType.setName(domainTypeName);

                            addType(schemaName, domainTypeName, domainType);
                        }

                        if (schemaDefinitionResourceRelativePath.contains("/" + NAME_OF_MIGRATIONS_DIRECTORY + "/")) {
                            String migrationName = schemaDefinitionResourceRelativePath.substring(
                                    schemaDefinitionResourceRelativePath.lastIndexOf("/"),
                                    schemaDefinitionResourceRelativePath.lastIndexOf("."));
                            Migration migration = LiteQLJsonUtil.toBean(
                                    schemaDefinitionContent.getValue(), Migration.class);
                            migration.setName(migrationName);
                            migration.setSchema(schemaName);
                            migration.setDomainType(domainTypeName);

                            addMigration(schemaName, migrationName, migration);
                        }
                    }
                }
            } catch (IOException ex) {
                throw new IllegalArgumentException(
                        "Location patterns [" + locationPattern + "] invalid, " + ex.getMessage(), ex);
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