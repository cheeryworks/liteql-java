package org.cheeryworks.liteql.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.query.PublicQuery;
import org.cheeryworks.liteql.query.QueryConditions;
import org.cheeryworks.liteql.query.enums.ConditionClause;
import org.cheeryworks.liteql.query.enums.ConditionOperator;
import org.cheeryworks.liteql.query.enums.ConditionType;
import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.query.read.field.FieldDefinitions;
import org.cheeryworks.liteql.query.read.result.ReadResult;
import org.cheeryworks.liteql.query.read.result.TreeReadResult;
import org.cheeryworks.liteql.query.read.result.TreeReadResults;
import org.cheeryworks.liteql.query.save.SaveQueryAssociations;
import org.cheeryworks.liteql.schema.Type;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.enums.DataType;
import org.cheeryworks.liteql.schema.enums.MigrationOperationType;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.schema.migration.operation.MigrationOperation;
import org.cheeryworks.liteql.util.jackson.deserializer.ConditionClauseDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.ConditionOperatorDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.ConditionTypeDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.DataTypeDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.FieldDefinitionsDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.FieldDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.MigrationOperationDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.MigrationOperationTypeDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.PublicQueryDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.QueryConditionsDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.QueryTypeDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.SaveQueryAssociationsDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.TypeDeserializer;
import org.cheeryworks.liteql.util.jackson.deserializer.TypeNameDeserializer;
import org.cheeryworks.liteql.util.jackson.serializer.ConditionClauseSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.ConditionOperatorSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.ConditionTypeSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.DataTypeSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.MigrationOperationTypeSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.QueryTypeSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.TypeNameSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.TypeSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.cheeryworks.liteql.schema.HierarchicalEntity.CHILDREN_FIELD_NAME;
import static org.cheeryworks.liteql.schema.HierarchicalEntity.LEAF_FIELD_NAME;
import static org.cheeryworks.liteql.schema.HierarchicalEntity.PARENT_ID_FIELD_NAME;
import static org.cheeryworks.liteql.schema.SortableEntity.PRIORITY_FIELD_NAME;
import static org.cheeryworks.liteql.schema.SortableEntity.SORT_CODE_FIELD_NAME;
import static org.cheeryworks.liteql.schema.field.IdField.ID_FIELD_NAME;

public abstract class LiteQLUtil {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        OBJECT_MAPPER.registerModule(getLiteQLJsonModule());
    }

    private LiteQLUtil() {

    }

    public static SimpleModule getLiteQLJsonModule() {
        SimpleModule module = new SimpleModule();

        module.addDeserializer(TypeName.class, new TypeNameDeserializer());
        module.addDeserializer(Type.class, new TypeDeserializer());
        module.addDeserializer(Field.class, new FieldDeserializer());
        module.addDeserializer(MigrationOperation.class, new MigrationOperationDeserializer());
        module.addDeserializer(MigrationOperationType.class, new MigrationOperationTypeDeserializer());
        module.addDeserializer(QueryType.class, new QueryTypeDeserializer());
        module.addDeserializer(DataType.class, new DataTypeDeserializer());
        module.addDeserializer(FieldDefinitions.class, new FieldDefinitionsDeserializer());
        module.addDeserializer(QueryConditions.class, new QueryConditionsDeserializer());
        module.addDeserializer(ConditionClause.class, new ConditionClauseDeserializer());
        module.addDeserializer(ConditionOperator.class, new ConditionOperatorDeserializer());
        module.addDeserializer(ConditionType.class, new ConditionTypeDeserializer());
        module.addDeserializer(SaveQueryAssociations.class, new SaveQueryAssociationsDeserializer());
        module.addDeserializer(PublicQuery.class, new PublicQueryDeserializer());

        module.addSerializer(TypeName.class, new TypeNameSerializer());
        module.addSerializer(Type.class, new TypeSerializer());
        module.addSerializer(MigrationOperationType.class, new MigrationOperationTypeSerializer());
        module.addSerializer(QueryType.class, new QueryTypeSerializer());
        module.addSerializer(DataType.class, new DataTypeSerializer());
        module.addSerializer(ConditionClause.class, new ConditionClauseSerializer());
        module.addSerializer(ConditionOperator.class, new ConditionOperatorSerializer());
        module.addSerializer(ConditionType.class, new ConditionTypeSerializer());

        return module;
    }

    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    public static TypeName getTypeName(String domainTypeName) {
        String[] typeNameParts = domainTypeName.split("\\.");

        TypeName typeName = new TypeName();
        typeName.setSchema(typeNameParts[0]);
        typeName.setName(typeNameParts[1]);

        return typeName;
    }

    public static <T> T toBean(String content, Class<T> clazz) {
        T bean = null;

        if (StringUtils.isNotBlank(content)) {
            try {
                bean = OBJECT_MAPPER.readValue(content, clazz);
            } catch (Exception ex) {
                throw new IllegalArgumentException("\"" + content + "\" is not valid JSON", ex);
            }
        }

        return bean;
    }

    public static <T> String toJson(T bean) {
        try {
            return OBJECT_MAPPER.writeValueAsString(bean);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    public static JsonNode toJsonNode(ObjectMapper objectMapper, String content) {
        if (StringUtils.isNotBlank(content)) {
            try {
                return objectMapper.readTree(content);
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }

        return null;
    }

    public static Set<String> convertDelimitedParameterToSetOfString(String ids) {
        Set<String> selectedIds = new LinkedHashSet<>();
        if (!StringUtils.isEmpty(ids)) {
            String[] idsInArray = ids.split("[,]");
            for (String id : idsInArray) {
                selectedIds.add(id);
            }
        }

        return selectedIds;
    }

    public static Set<Long> convertDelimitedParameterToSetOfLong(String ids) {
        Set<Long> selectedIds = new HashSet<>();
        if (!StringUtils.isEmpty(ids)) {
            String[] idsInArray = ids.split("[,]");
            for (String id : idsInArray) {
                selectedIds.add(Long.parseLong(id));
            }
        }

        return selectedIds;
    }

    public static String httpUrlConcat(String baseUrl, String path) {
        if (baseUrl == null || path == null) {
            throw new IllegalArgumentException("baseUrl and path must not be null");
        }

        if (!baseUrl.trim().endsWith("/")) {
            baseUrl = baseUrl.trim() + "/";
        }

        if (path.trim().startsWith("/")) {
            path = path.trim().substring(1);
        }

        return baseUrl + path;
    }

    public static String camelNameToLowerDashConnectedLowercaseName(String camelName) {
        String[] words = StringUtils.splitByCharacterTypeCamelCase(camelName);

        return String.join("_", words).toLowerCase();
    }

    public static String lowerDashConnectedLowercaseNameToUncapitalizedCamelName(
            String lowerDashConnectedLowercaseName) {
        String[] words = lowerDashConnectedLowercaseName.split("_");

        List<String> capitalizedWords = new ArrayList<>();

        for (String word : words) {
            capitalizedWords.add(StringUtils.capitalize(word));
        }

        return StringUtils.uncapitalize(
                String.join("", capitalizedWords.toArray(new String[capitalizedWords.size()])));
    }

    public static String plural(String word) {
        return English.plural(word);
    }

    public static List<Map<String, Object>> sortInTree(
            List<Map<String, Object>> dataSet) {
        List<Map<String, Object>> sortedMapsInTree = new LinkedList<>();

        if (CollectionUtils.isNotEmpty(dataSet)) {
            Map<String, Object> firstRow = dataSet.get(0);

            Set<String> parentKeyFieldNames = new HashSet<>();

            for (String fieldName : firstRow.keySet()) {
                if (!fieldName.equals(PARENT_ID_FIELD_NAME)) {
                    parentKeyFieldNames.add(fieldName);
                }
            }

            int priority = 1;
            long sortCode = 1001;
            for (Map<String, Object> data : dataSet) {
                boolean isRoot = false;
                for (String parentKeyFieldName : parentKeyFieldNames) {
                    Object parentKeyFieldValue = data.get(parentKeyFieldName);
                    if (parentKeyFieldValue == null || StringUtils.isBlank(parentKeyFieldValue.toString())) {
                        data.put(PRIORITY_FIELD_NAME, priority);
                        data.put(SORT_CODE_FIELD_NAME, String.valueOf(sortCode));
                        data.put(LEAF_FIELD_NAME, true);
                        sortedMapsInTree.add(data);

                        priority++;
                        sortCode++;

                        isRoot = true;

                        break;
                    }
                }

                if (isRoot) {
                    generateSortCodeForMap(dataSet, data, parentKeyFieldNames);
                }
            }
        }

        return sortedMapsInTree;
    }

    private static void generateSortCodeForMap(
            List<Map<String, Object>> dataSet, Map<String, Object> sortedData, Set<String> parentKeyFieldNames) {
        int priority = 1;
        String parentSortCode = (String) sortedData.get(SORT_CODE_FIELD_NAME);
        long sortCode = 1001;
        for (Map<String, Object> data : dataSet) {
            boolean matched = true;

            for (String parentKeyFieldName : parentKeyFieldNames) {
                Object parentKeyFieldValue = data.get(parentKeyFieldName);

                if (parentKeyFieldValue == null
                        || StringUtils.isBlank(parentKeyFieldValue.toString())
                        || !parentKeyFieldValue.equals(
                        sortedData.get(ID_FIELD_NAME))) {
                    matched = false;

                    break;
                }
            }

            if (matched) {
                data.put(PRIORITY_FIELD_NAME, priority);
                data.put(SORT_CODE_FIELD_NAME, parentSortCode + sortCode);
                data.put(LEAF_FIELD_NAME, true);

                sortedData.put(LEAF_FIELD_NAME, false);

                addChild(sortedData, data);
                priority++;
                sortCode++;

                generateSortCodeForMap(
                        dataSet, data, parentKeyFieldNames);
            }
        }
    }

    public static void treeToList(
            List<Map<String, Object>> sortedDataInTree, List<Map<String, Object>> dataSet) {
        for (Map<String, Object> sortedMapInTree : sortedDataInTree) {
            dataSet.add(sortedMapInTree);
            if (sortedMapInTree.get(CHILDREN_FIELD_NAME) != null
                    && ((List<Map<String, Object>>) sortedMapInTree.get(CHILDREN_FIELD_NAME)).size() > 0) {
                treeToList((List<Map<String, Object>>) sortedMapInTree.get(CHILDREN_FIELD_NAME), dataSet);
            }
        }

        for (Map<String, Object> data : dataSet) {
            data.remove(CHILDREN_FIELD_NAME);
        }
    }

    private static void addChild(Map<String, Object> parent, Map<String, Object> child) {
        List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get(CHILDREN_FIELD_NAME);

        if (children == null) {
            children = new LinkedList<>();
            parent.put(CHILDREN_FIELD_NAME, children);
        }

        children.add(child);
    }

    public static TreeReadResults transformInTree(List<ReadResult> results) {
        List<TreeReadResult> hierarchicalEntities = hierarchicalEntitiesInTreeForMap(results);

        return new TreeReadResults(hierarchicalEntities);
    }

    private static List<TreeReadResult> hierarchicalEntitiesInTreeForMap(List<ReadResult> hierarchicalEntities) {
        List<TreeReadResult> hierarchicalEntitiesInTree = new LinkedList<>();

        if (hierarchicalEntities != null && hierarchicalEntities.size() > 0) {
            for (Map<String, Object> hierarchicalEntity : hierarchicalEntities) {
                boolean isRoot = true;

                String rootSortCode = (String) hierarchicalEntity.get(SORT_CODE_FIELD_NAME);

                if (rootSortCode.length() > 4) {
                    for (Map<String, Object> j : hierarchicalEntities) {
                        String childSortCode = (String) j.get(SORT_CODE_FIELD_NAME);
                        if (childSortCode.equals(rootSortCode.substring(0, rootSortCode.length() - 4))) {
                            isRoot = false;
                            break;
                        }
                    }
                }

                if (isRoot) {
                    hierarchicalEntity.put(LEAF_FIELD_NAME, true);
                    hierarchicalEntitiesInTree.add(new TreeReadResult(hierarchicalEntity));
                }
            }

            hierarchicalEntitiesInTreeForMap(hierarchicalEntities, hierarchicalEntitiesInTree);
        }

        return hierarchicalEntitiesInTree;
    }

    private static void hierarchicalEntitiesInTreeForMap(
            List<ReadResult> hierarchicalEntities, List<TreeReadResult> hierarchicalEntitiesInTree) {
        if (hierarchicalEntitiesInTree == null) {
            return;
        }

        for (TreeReadResult hierarchicalEntityInTree : hierarchicalEntitiesInTree) {
            String parentSortCode = (String) hierarchicalEntityInTree.get(SORT_CODE_FIELD_NAME);
            for (Map<String, Object> hierarchicalEntity : hierarchicalEntities) {
                String childSortCode = (String) hierarchicalEntity.get(SORT_CODE_FIELD_NAME);
                if (childSortCode.startsWith(parentSortCode)
                        && childSortCode.length() == parentSortCode.length() + 4) {
                    hierarchicalEntityInTree.put(LEAF_FIELD_NAME, false);

                    hierarchicalEntity.put(LEAF_FIELD_NAME, true);

                    LinkedList<TreeReadResult> children = hierarchicalEntityInTree.getChildren();

                    if (children == null) {
                        children = new LinkedList<>();
                        hierarchicalEntityInTree.setChildren(children);
                    }

                    children.add(new TreeReadResult(hierarchicalEntity));
                }
            }

            hierarchicalEntitiesInTreeForMap(
                    hierarchicalEntities,
                    hierarchicalEntityInTree.getChildren());
        }
    }

}
