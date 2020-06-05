package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.model.type.field.ReferenceField;
import org.cheeryworks.liteql.model.type.field.BlobField;
import org.cheeryworks.liteql.model.type.field.BooleanField;
import org.cheeryworks.liteql.model.type.field.ClobField;
import org.cheeryworks.liteql.model.type.field.DecimalField;
import org.cheeryworks.liteql.model.type.field.IdField;
import org.cheeryworks.liteql.model.type.field.IntegerField;
import org.cheeryworks.liteql.model.type.field.StringField;
import org.cheeryworks.liteql.model.type.field.TimestampField;
import org.apache.commons.lang3.StringUtils;

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
                        return LiteQLJsonUtil.toBean(node.toString(), IdField.class);
                    case String:
                        return LiteQLJsonUtil.toBean(node.toString(), StringField.class);
                    case Integer:
                        return LiteQLJsonUtil.toBean(node.toString(), IntegerField.class);
                    case Timestamp:
                        return LiteQLJsonUtil.toBean(node.toString(), TimestampField.class);
                    case Boolean:
                        return LiteQLJsonUtil.toBean(node.toString(), BooleanField.class);
                    case Decimal:
                        return LiteQLJsonUtil.toBean(node.toString(), DecimalField.class);
                    case Clob:
                        return LiteQLJsonUtil.toBean(node.toString(), ClobField.class);
                    case Blob:
                        return LiteQLJsonUtil.toBean(node.toString(), BlobField.class);
                    case Reference:
                        return LiteQLJsonUtil.toBean(node.toString(), ReferenceField.class);
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
