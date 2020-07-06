package org.cheeryworks.liteql.service.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.StructType;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.type.migration.Migration;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PathMatchingResourceRepository implements Repository {

    private ObjectMapper objectMapper;

    private String[] locationPatterns;

    private Map<String, Map<String, DomainType>> domainTypeInSchemas = new LinkedHashMap<>();

    private Map<String, Map<String, StructType>> structTypeInSchemas = new LinkedHashMap<>();

    private Map<String, Map<String, Migration>> migrations = new LinkedHashMap<>();

    public PathMatchingResourceRepository(ObjectMapper objectMapper, String... locationPatterns) {
        this.objectMapper = objectMapper;
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

                    if (schemaDefinitions.get(schemaName) == null) {
                        schemaDefinitions.put(schemaName, new HashMap<>());
                        schemaPaths.put(schemaName, schemaRootResourcePath);
                    } else {
                        throw new IllegalArgumentException("Schema [" + schemaName + "]"
                                + " exist in path [" + schemaPaths.get(schemaName) + "]"
                                + ", but find in another path [" + schemaRootResourcePath + "]");
                    }
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

                        TypeName domainTypeName = new TypeName();

                        domainTypeName.setSchema(schemaName);
                        domainTypeName.setName(schemaDefinitionResourceRelativePath.split("/")[1]);

                        if (schemaDefinitionResourceRelativePath.endsWith(NAME_OF_TYPE_DEFINITION)) {
                            TypeName typeName = LiteQLJsonUtil.toBean(
                                    objectMapper, schemaDefinitionContent.getValue(), TypeName.class);

                            typeName.setSchema(schemaName);
                            typeName.setName(domainTypeName.getName());

                            if (typeName.isStruct()) {
                                addStructType((StructType) typeName);
                            } else {
                                addDomainType((DomainType) typeName);
                            }
                        }

                        if (schemaDefinitionResourceRelativePath.contains("/" + NAME_OF_MIGRATIONS_DIRECTORY + "/")) {
                            String migrationName = schemaDefinitionResourceRelativePath.substring(
                                    schemaDefinitionResourceRelativePath.lastIndexOf("/"),
                                    schemaDefinitionResourceRelativePath.lastIndexOf("."));
                            Migration migration = LiteQLJsonUtil.toBean(
                                    objectMapper, schemaDefinitionContent.getValue(), Migration.class);
                            migration.setName(migrationName);
                            migration.setDomainTypeName(domainTypeName);

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

    protected Map<String, Map<String, DomainType>> getDomainTypeInSchemas() {
        return domainTypeInSchemas;
    }

    protected Map<String, Map<String, StructType>> getStructTypeInSchemas() {
        return structTypeInSchemas;
    }

    protected Map<String, Map<String, Migration>> getMigrations() {
        return migrations;
    }

    protected void addStructType(StructType structType) {
        Map<String, StructType> structTypeInSchema = getStructTypeInSchemas().get(structType.getSchema());

        if (structTypeInSchema == null) {
            structTypeInSchema = new LinkedHashMap<>();
            getStructTypeInSchemas().put(structType.getSchema(), structTypeInSchema);
        }

        structTypeInSchema.put(structType.getName(), structType);
    }

    protected void addDomainType(DomainType domainType) {
        Map<String, DomainType> domainTypeInSchema = getDomainTypeInSchemas().get(domainType.getSchema());

        if (domainTypeInSchema == null) {
            domainTypeInSchema = new LinkedHashMap<>();
            getDomainTypeInSchemas().put(domainType.getSchema(), domainTypeInSchema);
        }

        domainTypeInSchema.put(domainType.getName(), domainType);
    }

    private void addMigration(String schemaName, String migrationName, Migration migration) {
        Map<String, Migration> migrationsInSchema = getMigrations().get(schemaName);

        if (migrationsInSchema == null) {
            migrationsInSchema = new TreeMap<>(String::compareToIgnoreCase);
            getMigrations().put(schemaName, migrationsInSchema);
        }

        migrationsInSchema.put(migrationName, migration);
    }

    @Override
    public Set<String> getSchemas() {
        return getDomainTypeInSchemas().keySet();
    }

    @Override
    public Map<String, StructType> getStructTypes(String schemaName) {
        Map<String, StructType> structTypes = getStructTypeInSchemas().get(schemaName);

        if (structTypes == null) {
            throw new IllegalStateException("Can not get schema [" + schemaName + "]");
        }

        return structTypes;
    }

    @Override
    public Map<String, DomainType> getDomainTypes(String schemaName) {
        Map<String, DomainType> domainTypes = getDomainTypeInSchemas().get(schemaName);

        if (domainTypes == null) {
            throw new IllegalStateException("Can not get schema [" + schemaName + "]");
        }

        return domainTypes;
    }

    @Override
    public StructType getStructType(TypeName typeName) {
        Assert.notNull(typeName, "TypeName is required");

        Map<String, StructType> structTypes = getStructTypeInSchemas().get(typeName.getSchema());

        if (structTypes == null) {
            throw new IllegalStateException("Can not get schema [" + typeName.getSchema() + "]");
        }

        StructType structType = structTypes.get(typeName.getName());

        if (structType == null) {
            throw new IllegalStateException("Can not get struct type [" + typeName.getFullname() + "]");
        }

        return structType;
    }

    @Override
    public DomainType getDomainType(TypeName typeName) {
        Assert.notNull(typeName, "TypeName is required");

        Map<String, DomainType> domainTypes = getDomainTypeInSchemas().get(typeName.getSchema());

        if (domainTypes == null) {
            throw new IllegalStateException("Can not get schema [" + typeName.getSchema() + "]");
        }

        DomainType domainType = domainTypes.get(typeName.getName());

        if (domainType == null) {
            throw new IllegalStateException("Can not get domain type [" + typeName.getFullname() + "]");
        }

        return domainType;
    }

    @Override
    public List<Migration> getMigrations(String schemaName) {
        Map<String, Migration> migrationsInSchema = getMigrations().get(schemaName);

        if (MapUtils.isNotEmpty(migrationsInSchema)) {
            return Arrays.asList(migrationsInSchema.values().toArray(new Migration[migrationsInSchema.size()]));
        }

        return Collections.emptyList();
    }

}
