package org.cheeryworks.liteql.model.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.field.BlobField;
import org.cheeryworks.liteql.model.type.field.BooleanField;
import org.cheeryworks.liteql.model.type.field.ClobField;
import org.cheeryworks.liteql.model.type.field.DecimalField;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.field.IdField;
import org.cheeryworks.liteql.model.type.field.IntegerField;
import org.cheeryworks.liteql.model.type.field.ReferenceField;
import org.cheeryworks.liteql.model.type.field.StringField;
import org.cheeryworks.liteql.model.type.field.TimestampField;

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
                        return jsonParser.getCodec().treeToValue(node, IdField.class);
                    case String:
                        return jsonParser.getCodec().treeToValue(node, StringField.class);
                    case Integer:
                        return jsonParser.getCodec().treeToValue(node, IntegerField.class);
                    case Timestamp:
                        return jsonParser.getCodec().treeToValue(node, TimestampField.class);
                    case Boolean:
                        return jsonParser.getCodec().treeToValue(node, BooleanField.class);
                    case Decimal:
                        return jsonParser.getCodec().treeToValue(node, DecimalField.class);
                    case Clob:
                        return jsonParser.getCodec().treeToValue(node, ClobField.class);
                    case Blob:
                        return jsonParser.getCodec().treeToValue(node, BlobField.class);
                    case Reference:
                        return jsonParser.getCodec().treeToValue(node, ReferenceField.class);
                    default:
                        throw new IllegalArgumentException("Unsupported field type: " + dataTypeInString);
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException("Unsupported field type: " + dataTypeInString);
            }
        } else {
            throw new IllegalArgumentException("Field type name not specified");
        }
    }

}
