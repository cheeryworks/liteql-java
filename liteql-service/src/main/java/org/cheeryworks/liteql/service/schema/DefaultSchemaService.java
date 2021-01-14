package org.cheeryworks.liteql.service.schema;

import org.apache.commons.io.IOUtils;
import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.util.LiteQL;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static org.cheeryworks.liteql.schema.Schema.NAME_OF_MIGRATIONS_DIRECTORY;
import static org.cheeryworks.liteql.schema.Schema.NAME_OF_TYPES_DIRECTORY;
import static org.cheeryworks.liteql.schema.Schema.SUFFIX_OF_TYPE_DEFINITION;

public class DefaultSchemaService extends AbstractSchemaService {

    public DefaultSchemaService(LiteQLProperties liteQLProperties) {
        super(liteQLProperties);

        if (liteQLProperties.isJsonBasedSchemaEnabled()) {
            init();
        }
    }

    private void init() {
        Map<String, SchemaMetadata> schemaMetadataSet = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        try {
            Reflections reflections = new Reflections(
                    LiteQL.Constants.SCHEMA_DEFINITION_CLASSPATH_ROOT, new ResourcesScanner());

            Set<String> schemaRootPaths = reflections.getResources(Pattern.compile(".*\\.yml"));

            Map<String, String> schemaPathMapping = new HashMap<>();

            for (String schemaRootPath : schemaRootPaths) {
                String schemaName = schemaRootPath
                        .substring(schemaRootPath.lastIndexOf("/") + 1, schemaRootPath.lastIndexOf("."));

                if (schemaMetadataSet.get(schemaName) == null) {
                    schemaMetadataSet.put(schemaName, new SchemaMetadata(schemaName));
                    schemaPathMapping.put(schemaName, schemaRootPath.substring(0, schemaRootPath.lastIndexOf(".")));
                } else {
                    throw new IllegalArgumentException("Schema [" + schemaName + "]"
                            + " exist in path [" + schemaPathMapping.get(schemaName) + "]"
                            + ", but find in another path [" + schemaRootPath + "]");
                }
            }

            Set<String> schemaDefinitionResourcePaths
                    = reflections.getResources(Pattern.compile(".*\\.json"));

            for (String schemaDefinitionResourcePath : schemaDefinitionResourcePaths) {
                for (Map.Entry<String, String> schemaPathEntry : schemaPathMapping.entrySet()) {
                    if (schemaDefinitionResourcePath.startsWith(
                            schemaPathEntry.getValue() + "/" + NAME_OF_TYPES_DIRECTORY)) {
                        String schemaDefinitionResourceRelativePath
                                = schemaDefinitionResourcePath.substring(schemaPathEntry.getValue().length() + 1);

                        if (!schemaDefinitionResourceRelativePath.contains(NAME_OF_TYPES_DIRECTORY)) {
                            continue;
                        }

                        SchemaMetadata schemaMetadata = schemaMetadataSet.get(schemaPathEntry.getKey());

                        String typeName = schemaDefinitionResourceRelativePath.split("/")[1];

                        TypeMetadata typeMetadata = schemaMetadata.getTypeMetadataSet().get(typeName);

                        if (typeMetadata == null) {
                            typeMetadata = new TypeMetadata(typeName);

                            schemaMetadata.getTypeMetadataSet().put(typeName, typeMetadata);
                        }

                        String contentName = schemaDefinitionResourceRelativePath.substring(
                                schemaDefinitionResourceRelativePath.lastIndexOf("/") + 1,
                                schemaDefinitionResourceRelativePath.lastIndexOf("."));

                        String content = IOUtils.toString(
                                getClass().getClassLoader().getResourceAsStream(schemaDefinitionResourcePath),
                                StandardCharsets.UTF_8);

                        if (schemaDefinitionResourceRelativePath.endsWith(SUFFIX_OF_TYPE_DEFINITION)) {
                            typeMetadata.getContents().put(contentName, content);
                        }

                        if (schemaDefinitionResourceRelativePath
                                .contains("/" + NAME_OF_MIGRATIONS_DIRECTORY + "/")) {
                            typeMetadata.getMigrationContents().put(contentName, content);
                        }

                        break;
                    }
                }
            }

        } catch (IOException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }

        for (SchemaMetadata schemaMetadata : schemaMetadataSet.values()) {
            processSchemaMetadata(schemaMetadata);
        }
    }

}
