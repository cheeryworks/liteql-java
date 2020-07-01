package org.cheeryworks.liteql.model.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.enums.ConditionClause;
import org.cheeryworks.liteql.model.enums.ConditionOperator;
import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.model.enums.MigrationOperationType;
import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.query.PublicQuery;
import org.cheeryworks.liteql.model.query.QueryConditions;
import org.cheeryworks.liteql.model.query.read.field.FieldDefinitions;
import org.cheeryworks.liteql.model.query.save.SaveQueryAssociations;
import org.cheeryworks.liteql.model.type.DomainTypeName;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.migration.operation.MigrationOperation;
import org.cheeryworks.liteql.model.util.jackson.deserializer.ConditionClauseDeserializer;
import org.cheeryworks.liteql.model.util.jackson.deserializer.ConditionOperatorDeserializer;
import org.cheeryworks.liteql.model.util.jackson.deserializer.ConditionTypeDeserializer;
import org.cheeryworks.liteql.model.util.jackson.deserializer.DomainTypeNameDeserializer;
import org.cheeryworks.liteql.model.util.jackson.deserializer.FieldDefinitionsDeserializer;
import org.cheeryworks.liteql.model.util.jackson.deserializer.FieldDeserializer;
import org.cheeryworks.liteql.model.util.jackson.deserializer.MigrationOperationDeserializer;
import org.cheeryworks.liteql.model.util.jackson.deserializer.MigrationOperationTypeDeserializer;
import org.cheeryworks.liteql.model.util.jackson.deserializer.PublicQueryDeserializer;
import org.cheeryworks.liteql.model.util.jackson.deserializer.QueryConditionsDeserializer;
import org.cheeryworks.liteql.model.util.jackson.deserializer.QueryTypeDeserializer;
import org.cheeryworks.liteql.model.util.jackson.deserializer.SaveQueryAssociationsDeserializer;
import org.cheeryworks.liteql.model.util.jackson.serializer.ConditionClauseSerializer;
import org.cheeryworks.liteql.model.util.jackson.serializer.ConditionOperatorSerializer;
import org.cheeryworks.liteql.model.util.jackson.serializer.ConditionTypeSerializer;
import org.cheeryworks.liteql.model.util.jackson.serializer.DomainTypeNameSerializer;
import org.cheeryworks.liteql.model.util.jackson.serializer.MigrationOperationTypeSerializer;
import org.cheeryworks.liteql.model.util.jackson.serializer.QueryTypeSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class LiteQLJsonUtil {

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
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            JsonGenerator jsonGenerator = objectMapper.getFactory()
                    .createGenerator(outputStream, JsonEncoding.UTF8).useDefaultPrettyPrinter();

            jsonGenerator.writeObject(bean);

            return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    public static JsonNode toJsonNode(ObjectMapper objectMapper, String content) {
        if (StringUtils.isNotBlank(content)) {
            try {
                return objectMapper.getFactory().createParser(content).readValueAsTree();
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
        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);

        builder.deserializerByType(ConditionClause.class, new ConditionClauseDeserializer());
        builder.deserializerByType(ConditionOperator.class, new ConditionOperatorDeserializer());
        builder.deserializerByType(ConditionType.class, new ConditionTypeDeserializer());
        builder.deserializerByType(DomainTypeName.class, new DomainTypeNameDeserializer());
        builder.deserializerByType(FieldDefinitions.class, new FieldDefinitionsDeserializer());
        builder.deserializerByType(Field.class, new FieldDeserializer());
        builder.deserializerByType(MigrationOperation.class, new MigrationOperationDeserializer());
        builder.deserializerByType(MigrationOperationType.class, new MigrationOperationTypeDeserializer());
        builder.deserializerByType(PublicQuery.class, new PublicQueryDeserializer());
        builder.deserializerByType(QueryConditions.class, new QueryConditionsDeserializer());
        builder.deserializerByType(QueryType.class, new QueryTypeDeserializer());
        builder.deserializerByType(SaveQueryAssociations.class, new SaveQueryAssociationsDeserializer());

        builder.serializerByType(ConditionClause.class, new ConditionClauseSerializer());
        builder.serializerByType(ConditionOperator.class, new ConditionOperatorSerializer());
        builder.serializerByType(ConditionType.class, new ConditionTypeSerializer());
        builder.serializerByType(DomainTypeName.class, new DomainTypeNameSerializer());
        builder.serializerByType(MigrationOperationType.class, new MigrationOperationTypeSerializer());
        builder.serializerByType(QueryType.class, new QueryTypeSerializer());
    }

}