package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.cheeryworks.liteql.model.enums.ConditionOperator;
import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.enums.StandardConditionClause;
import org.cheeryworks.liteql.model.type.migration.MigrationOperation;
import org.cheeryworks.liteql.model.query.Queries;
import org.cheeryworks.liteql.model.query.condition.ConditionType;
import org.cheeryworks.liteql.model.query.condition.QueryConditions;
import org.cheeryworks.liteql.model.query.field.QueryFieldDefinitions;
import org.cheeryworks.liteql.model.type.DomainTypeField;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class LiteQLJsonUtil {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(DomainTypeField.class, new FieldDeserializer());
        module.addDeserializer(MigrationOperation.class, new MigrationOperationDeserializer());
        module.addDeserializer(QueryType.class, new QueryTypeDeserializer());
        module.addDeserializer(QueryFieldDefinitions.class, new FieldDefinitionsDeserializer());
        module.addDeserializer(QueryConditions.class, new QueryConditionsDeserializer());
        module.addDeserializer(Queries.class, new QueriesDeserializer());
        module.addDeserializer(StandardConditionClause.class, new ConditionClauseDeserializer());
        module.addDeserializer(ConditionType.class, new ConditionTypeDeserializer());
        module.addDeserializer(ConditionOperator.class, new ConditionOperatorDeserializer());
        module.addSerializer(QueryType.class, new QueryTypeSerializer());
        module.addSerializer(StandardConditionClause.class, new ConditionClauseSerializer());
        module.addSerializer(ConditionType.class, new ConditionTypeSerializer());
        module.addSerializer(ConditionOperator.class, new ConditionOperatorSerializer());

        OBJECT_MAPPER.registerModule(module);
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
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            JsonGenerator jsonGenerator = OBJECT_MAPPER.getFactory()
                    .createGenerator(outputStream, JsonEncoding.UTF8).useDefaultPrettyPrinter();

            jsonGenerator.writeObject(bean);

            return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    public static JsonNode toJsonNode(String content) {
        if (StringUtils.isNotBlank(content)) {
            try {
                return OBJECT_MAPPER.getFactory().createParser(content).readValueAsTree();
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }

        return null;
    }

}
