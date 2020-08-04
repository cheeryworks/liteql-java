package org.cheeryworks.liteql.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.cheeryworks.liteql.query.read.field.FieldDefinition;
import org.cheeryworks.liteql.query.read.field.FieldDefinitions;

import java.io.IOException;
import java.util.Iterator;

public class FieldDefinitionsDeserializer extends StdDeserializer<FieldDefinitions> {

    public FieldDefinitionsDeserializer() {
        super(FieldDefinitions.class);
    }

    @Override
    public FieldDefinitions deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        FieldDefinitions fieldDefinitions = new FieldDefinitions();

        JsonNode fields = jsonParser.getCodec().readTree(jsonParser);

        if (fields != null) {
            if (fields instanceof ArrayNode) {
                for (JsonNode field : fields) {
                    FieldDefinition fieldDefinition = new FieldDefinition();

                    if (field instanceof ValueNode) {
                        fieldDefinition.setName(field.asText());
                    } else if (field instanceof ObjectNode) {
                        fieldDefinition = jsonParser.getCodec().treeToValue(field, FieldDefinition.class);
                    } else {
                        throw new IllegalArgumentException(
                                "Fields definition not supported: \n" + fields.asText());
                    }

                    fieldDefinitions.add(fieldDefinition);
                }
            } else if (fields instanceof ObjectNode) {
                Iterator<String> fieldNameIterator = fields.fieldNames();
                while (fieldNameIterator.hasNext()) {
                    String fieldName = fieldNameIterator.next();
                    String fieldAlias = fields.get(fieldName).asText();

                    fieldDefinitions.add(new FieldDefinition(fieldName, fieldAlias, fieldAlias));
                }
            } else {
                throw new IllegalArgumentException(
                        "Fields definition not supported: \n" + fields.asText());
            }
        }

        return fieldDefinitions;
    }

}
