package org.cheeryworks.liteql.skeleton.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.skeleton.schema.enums.DataType;

import java.io.IOException;

public class DataTypeDeserializer extends StdDeserializer<DataType> {

    public DataTypeDeserializer() {
        super(DataType.class);
    }

    @Override
    public DataType deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String dataTypeInString = jsonParser.getValueAsString();

        if (StringUtils.isNotBlank(dataTypeInString)) {
            for (DataType dataType : DataType.values()) {
                if (dataType.name().toLowerCase().equals(dataTypeInString.toLowerCase())) {
                    return dataType;
                }
            }

            throw new IllegalArgumentException(
                    "Unsupported data type: " + dataTypeInString);

        } else {
            throw new IllegalArgumentException("Data type not specified");
        }
    }

}
