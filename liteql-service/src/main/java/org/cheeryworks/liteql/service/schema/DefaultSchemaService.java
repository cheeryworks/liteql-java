package org.cheeryworks.liteql.service.schema;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.LiteQLProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DefaultSchemaService extends AbstractSchemaService {

    private String[] locationPatterns;

    public DefaultSchemaService(LiteQLProperties liteQLProperties, String... locationPatterns) {
        super(liteQLProperties);

        this.locationPatterns = locationPatterns;

        init();
    }

    private void init() {
        Map<String, SchemaDefinition> schemaDefinitions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (String locationPattern : locationPatterns) {
            try {
                locationPattern = StringUtils.removeEnd(
                        org.springframework.util.StringUtils.cleanPath(locationPattern), "/");

                PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver
                        = new PathMatchingResourcePatternResolver(getClass().getClassLoader());

                Resource[] schemaRootResources
                        = pathMatchingResourcePatternResolver.getResources(locationPattern + "/*.yml");

                Map<String, String> schemaPaths = new HashMap<>();

                for (Resource schemaRootResource : schemaRootResources) {
                    String schemaName = schemaRootResource.getFilename()
                            .substring(0, schemaRootResource.getFilename().lastIndexOf("."));
                    String schemaRootResourcePath = schemaRootResource.getURL().getPath()
                            .substring(0, schemaRootResource.getURL().getPath().lastIndexOf("."));

                    if (schemaDefinitions.get(schemaName) == null) {
                        schemaDefinitions.put(schemaName, new SchemaDefinition(schemaName));
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

                            if (!schemaDefinitionResourceRelativePath.contains(
                                    SchemaService.NAME_OF_TYPES_DIRECTORY)) {
                                continue;
                            }

                            SchemaDefinition schemaDefinition = schemaDefinitions.get(schemaPathEntry.getKey());

                            String typeName = schemaDefinitionResourceRelativePath.split("/")[1];

                            TypeDefinition typeDefinition = schemaDefinition.getTypeDefinitions().get(typeName);

                            if (typeDefinition == null) {
                                typeDefinition = new TypeDefinition(typeName);

                                schemaDefinition.getTypeDefinitions().put(typeName, typeDefinition);
                            }

                            if (schemaDefinitionResourceRelativePath.endsWith(NAME_OF_TYPE_DEFINITION)) {
                                typeDefinition.setContent(
                                        IOUtils.toString(
                                                schemaDefinitionResource.getInputStream(), StandardCharsets.UTF_8));
                            }

                            if (schemaDefinitionResourceRelativePath
                                    .contains("/" + NAME_OF_MIGRATIONS_DIRECTORY + "/")) {
                                String migrationName = schemaDefinitionResourceRelativePath.substring(
                                        schemaDefinitionResourceRelativePath.lastIndexOf("/"),
                                        schemaDefinitionResourceRelativePath.lastIndexOf("."));

                                MigrationDefinition migrationDefinition = new MigrationDefinition(
                                        migrationName,
                                        IOUtils.toString(
                                                schemaDefinitionResource.getInputStream(), StandardCharsets.UTF_8));

                                typeDefinition.getMigrationDefinitions().put(migrationName, migrationDefinition);
                            }

                            break;
                        }
                    }
                }
            } catch (IOException ex) {
                throw new IllegalArgumentException(
                        "Location patterns [" + locationPattern + "] invalid, " + ex.getMessage(), ex);
            }
        }

        for (SchemaDefinition schemaDefinition : schemaDefinitions.values()) {
            processSchemaDefinition(schemaDefinition);
        }
    }

}
