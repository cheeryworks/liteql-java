package org.cheeryworks.liteql.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.query.PublicQuery;
import org.cheeryworks.liteql.query.QueryConditions;
import org.cheeryworks.liteql.query.condition.converter.BooleanConditionValueConverter;
import org.cheeryworks.liteql.query.condition.converter.ConditionValueConverter;
import org.cheeryworks.liteql.query.condition.converter.DecimalConditionValueConverter;
import org.cheeryworks.liteql.query.condition.converter.FieldConditionValueConverter;
import org.cheeryworks.liteql.query.condition.converter.IntegerConditionValueConverter;
import org.cheeryworks.liteql.query.condition.converter.LongConditionValueConverter;
import org.cheeryworks.liteql.query.condition.converter.StringConditionValueConverter;
import org.cheeryworks.liteql.query.condition.converter.TimestampConditionValueConverter;
import org.cheeryworks.liteql.query.enums.ConditionClause;
import org.cheeryworks.liteql.query.enums.ConditionOperator;
import org.cheeryworks.liteql.query.enums.ConditionType;
import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.query.read.field.FieldDefinitions;
import org.cheeryworks.liteql.query.save.SaveQueryAssociations;
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
import org.cheeryworks.liteql.util.jackson.deserializer.TypeNameDeserializer;
import org.cheeryworks.liteql.util.jackson.serializer.ConditionClauseSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.ConditionOperatorSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.ConditionTypeSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.DataTypeSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.MigrationOperationTypeSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.QueryTypeSerializer;
import org.cheeryworks.liteql.util.jackson.serializer.TypeNameSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class LiteQLUtil {

    private static final Map<ConditionType, ConditionValueConverter> CONDITION_VALUE_CONVERTERS;

    static {
        Map<ConditionType, ConditionValueConverter> conditionValueConverters = new HashMap<>();

        conditionValueConverters.put(ConditionType.Field, new FieldConditionValueConverter());
        conditionValueConverters.put(ConditionType.String, new StringConditionValueConverter());
        conditionValueConverters.put(ConditionType.Long, new LongConditionValueConverter());
        conditionValueConverters.put(ConditionType.Integer, new IntegerConditionValueConverter());
        conditionValueConverters.put(ConditionType.Timestamp, new TimestampConditionValueConverter());
        conditionValueConverters.put(ConditionType.Boolean, new BooleanConditionValueConverter());
        conditionValueConverters.put(ConditionType.Decimal, new DecimalConditionValueConverter());

        CONDITION_VALUE_CONVERTERS = Collections.unmodifiableMap(conditionValueConverters);
    }

    private LiteQLUtil() {

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

    public static <T> T toBean(ObjectMapper objectMapper, String content, Class<T> clazz) {
        T bean = null;

        if (StringUtils.isNotBlank(content)) {
            try {
                bean = objectMapper.readValue(content, clazz);
            } catch (Exception ex) {
                throw new IllegalArgumentException("\"" + content + "\" is not valid JSON", ex);
            }
        }

        return bean;
    }

    public static <T> String toJson(ObjectMapper objectMapper, T bean) {
        try {
            return objectMapper.writeValueAsString(bean);
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

    public static void configureObjectMapper(Jackson2ObjectMapperBuilder builder) {
        builder.featuresToEnable(SerializationFeature.INDENT_OUTPUT);
        builder.featuresToDisable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);

        builder.deserializerByType(DataType.class, new DataTypeDeserializer());
        builder.deserializerByType(ConditionClause.class, new ConditionClauseDeserializer());
        builder.deserializerByType(ConditionOperator.class, new ConditionOperatorDeserializer());
        builder.deserializerByType(ConditionType.class, new ConditionTypeDeserializer());
        builder.deserializerByType(TypeName.class, new TypeNameDeserializer());
        builder.deserializerByType(FieldDefinitions.class, new FieldDefinitionsDeserializer());
        builder.deserializerByType(Field.class, new FieldDeserializer());
        builder.deserializerByType(MigrationOperation.class, new MigrationOperationDeserializer());
        builder.deserializerByType(MigrationOperationType.class, new MigrationOperationTypeDeserializer());
        builder.deserializerByType(PublicQuery.class, new PublicQueryDeserializer());
        builder.deserializerByType(QueryConditions.class, new QueryConditionsDeserializer());
        builder.deserializerByType(QueryType.class, new QueryTypeDeserializer());
        builder.deserializerByType(SaveQueryAssociations.class, new SaveQueryAssociationsDeserializer());

        builder.serializerByType(DataType.class, new DataTypeSerializer());
        builder.serializerByType(ConditionClause.class, new ConditionClauseSerializer());
        builder.serializerByType(ConditionOperator.class, new ConditionOperatorSerializer());
        builder.serializerByType(ConditionType.class, new ConditionTypeSerializer());
        builder.serializerByType(TypeName.class, new TypeNameSerializer());
        builder.serializerByType(MigrationOperationType.class, new MigrationOperationTypeSerializer());
        builder.serializerByType(QueryType.class, new QueryTypeSerializer());
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

    public static Object transformValue(ConditionType conditionType, Object value) {
        try {
            return CONDITION_VALUE_CONVERTERS.get(conditionType).convert(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

}
