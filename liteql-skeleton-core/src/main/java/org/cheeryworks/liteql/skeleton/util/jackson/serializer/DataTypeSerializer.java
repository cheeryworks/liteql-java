package org.cheeryworks.liteql.skeleton.util.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.skeleton.schema.enums.DataType;

import java.io.IOException;

public class DataTypeSerializer extends StdSerializer<DataType> {

    public DataTypeSerializer() {
        super(DataType.class);
    }

    @Override
    public void serialize(DataType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.name().toLowerCase());
    }

}
