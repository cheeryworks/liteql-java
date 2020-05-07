package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.cheeryworks.liteql.model.query.field.QueryFieldDefinition;
import org.cheeryworks.liteql.model.query.field.QueryFieldDefinitions;

import java.io.IOException;
import java.util.Iterator;

public class FieldDefinitionsDeserializer extends StdDeserializer<QueryFieldDefinitions> {

    public FieldDefinitionsDeserializer() {
        super(QueryFieldDefinitions.class);
    }

    @Override
    public QueryFieldDefinitions deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        QueryFieldDefinitions fieldDefinitions = new QueryFieldDefinitions();

        JsonNode fields = jsonParser.getCodec().readTree(jsonParser);

        if (fields != null) {
            if (fields instanceof ArrayNode) {
                for (JsonNode field : fields) {
                    QueryFieldDefinition fieldDefinition = new QueryFieldDefinition();

                    if (field instanceof ValueNode) {
                        fieldDefinition.setName(field.asText());
                    } else if (field instanceof ObjectNode) {
                        fieldDefinition = LiteQLJsonUtil.toBean(field.toString(), QueryFieldDefinition.class);
                    } else {
                        throw new IllegalArgumentException(
                                "Fields definition not supported: \n" + LiteQLJsonUtil.toJson(fields));
                    }

                    fieldDefinitions.add(fieldDefinition);
                }
            } else if (fields instanceof ObjectNode) {
                Iterator<String> fieldNameIterator = fields.fieldNames();
                while (fieldNameIterator.hasNext()) {
                    String fieldName = fieldNameIterator.next();
                    String fieldAlias = fields.get(fieldName).asText();

                    fieldDefinitions.add(new QueryFieldDefinition(fieldName, fieldAlias, fieldAlias));
                }
            } else {
                throw new IllegalArgumentException(
                        "Fields definition not supported: \n" + LiteQLJsonUtil.toJson(fields));
            }
        }

        return fieldDefinitions;
    }

}
