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

import static org.cheeryworks.liteql.schema.Schema.NAME_OF_MIGRATIONS_DIRECTORY;
import static org.cheeryworks.liteql.schema.Schema.NAME_OF_TYPES_DIRECTORY;
import static org.cheeryworks.liteql.schema.Schema.SUFFIX_OF_SCHEMA_ROOT_FILE;
import static org.cheeryworks.liteql.schema.Schema.SUFFIX_OF_TYPE_DEFINITION;

public class DefaultSchemaService extends AbstractSchemaService {

    private String[] locations;

    public DefaultSchemaService(LiteQLProperties liteQLProperties, String... locations) {
        super(liteQLProperties);

        this.locations = locations;

        init();
    }

    private void init() {
        Map<String, SchemaDefinition> schemaDefinitions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (String location : locations) {
            try {
                location = StringUtils.removeEnd(
                        org.springframework.util.StringUtils.cleanPath(location), "/");

                PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver
                        = new PathMatchingResourcePatternResolver(getClass().getClassLoader());

                Resource[] schemaRootResources = pathMatchingResourcePatternResolver.getResources(
                        location + "/*" + SUFFIX_OF_SCHEMA_ROOT_FILE);

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
                        = pathMatchingResourcePatternResolver.getResources(location + "/**/*.json");

                for (Resource schemaDefinitionResource : schemaDefinitionResources) {
                    String schemaDefinitionResourcePath = schemaDefinitionResource.getURL().getPath();

                    for (Map.Entry<String, String> schemaPathEntry : schemaPaths.entrySet()) {
                        if (schemaDefinitionResourcePath.startsWith(schemaPathEntry.getValue())) {
                            String schemaDefinitionResourceRelativePath
                                    = schemaDefinitionResourcePath.substring(schemaPathEntry.getValue().length() + 1);

                            if (!schemaDefinitionResourceRelativePath.contains(NAME_OF_TYPES_DIRECTORY)) {
                                continue;
                            }

                            SchemaDefinition schemaDefinition = schemaDefinitions.get(schemaPathEntry.getKey());

                            String typeName = schemaDefinitionResourceRelativePath.split("/")[1];

                            TypeDefinition typeDefinition = schemaDefinition.getTypeDefinitions().get(typeName);

                            if (typeDefinition == null) {
                                typeDefinition = new TypeDefinition(typeName);

                                schemaDefinition.getTypeDefinitions().put(typeName, typeDefinition);
                            }

                            String contentName = schemaDefinitionResourceRelativePath.substring(
                                    schemaDefinitionResourceRelativePath.lastIndexOf("/") + 1,
                                    schemaDefinitionResourceRelativePath.lastIndexOf("."));

                            String content = IOUtils.toString(
                                    schemaDefinitionResource.getInputStream(), StandardCharsets.UTF_8);

                            if (schemaDefinitionResourceRelativePath.endsWith(SUFFIX_OF_TYPE_DEFINITION)) {
                                typeDefinition.getContents().put(contentName, content);
                            }

                            if (schemaDefinitionResourceRelativePath
                                    .contains("/" + NAME_OF_MIGRATIONS_DIRECTORY + "/")) {
                                typeDefinition.getMigrationContents().put(contentName, content);
                            }

                            break;
                        }
                    }
                }
            } catch (IOException ex) {
                throw new IllegalArgumentException(
                        "Location [" + location + "] invalid, " + ex.getMessage(), ex);
            }
        }

        for (SchemaDefinition schemaDefinition : schemaDefinitions.values()) {
            processSchemaDefinition(schemaDefinition);
        }
    }

}
