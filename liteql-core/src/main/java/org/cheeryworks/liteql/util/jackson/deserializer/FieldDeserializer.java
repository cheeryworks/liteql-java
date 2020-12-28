package org.cheeryworks.liteql.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.schema.enums.DataType;
import org.cheeryworks.liteql.schema.field.IdField;
import org.cheeryworks.liteql.schema.field.internal.DefaultBlobField;
import org.cheeryworks.liteql.schema.field.internal.DefaultBooleanField;
import org.cheeryworks.liteql.schema.field.internal.DefaultClobField;
import org.cheeryworks.liteql.schema.field.internal.DefaultDecimalField;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.schema.field.internal.DefaultIdField;
import org.cheeryworks.liteql.schema.field.internal.DefaultIntegerField;
import org.cheeryworks.liteql.schema.field.internal.DefaultLongField;
import org.cheeryworks.liteql.schema.field.internal.DefaultReferenceField;
import org.cheeryworks.liteql.schema.field.internal.DefaultStringField;
import org.cheeryworks.liteql.schema.field.internal.DefaultTimestampField;

import java.io.IOException;

public class FieldDeserializer extends StdDeserializer<Field> {

    public FieldDeserializer() {
        super(Field.class);
    }

    @Override
    public Field deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String dataTypeInString = node.get("type").textValue();

        if (StringUtils.isNotBlank(dataTypeInString)) {
            try {
                DataType dataType = DataType.valueOf(StringUtils.capitalize(dataTypeInString));

                switch (dataType) {
                    case Id:
                    case String:
                        if (IdField.ID_FIELD_NAME.equalsIgnoreCase(node.get("name").asText())) {
                            return jsonParser.getCodec().treeToValue(node, DefaultIdField.class);
                        } else {
                            return jsonParser.getCodec().treeToValue(node, DefaultStringField.class);
                        }
                    case Long:
                        return jsonParser.getCodec().treeToValue(node, DefaultLongField.class);
                    case Integer:
                        return jsonParser.getCodec().treeToValue(node, DefaultIntegerField.class);
                    case Timestamp:
                        return jsonParser.getCodec().treeToValue(node, DefaultTimestampField.class);
                    case Boolean:
                        return jsonParser.getCodec().treeToValue(node, DefaultBooleanField.class);
                    case Decimal:
                        return jsonParser.getCodec().treeToValue(node, DefaultDecimalField.class);
                    case Clob:
                        return jsonParser.getCodec().treeToValue(node, DefaultClobField.class);
                    case Blob:
                        return jsonParser.getCodec().treeToValue(node, DefaultBlobField.class);
                    case Reference:
                        return jsonParser.getCodec().treeToValue(node, DefaultReferenceField.class);
                    default:
                        throw new IllegalArgumentException("Unsupported field type: " + dataTypeInString);
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        } else {
            throw new IllegalArgumentException("Field type name not specified");
        }
    }

}
