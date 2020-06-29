package org.cheeryworks.liteql.model.util.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.model.enums.ConditionType;

import java.io.IOException;

public class ConditionTypeSerializer extends StdSerializer<ConditionType> {

    public ConditionTypeSerializer() {
        super(ConditionType.class);
    }

    @Override
    public void serialize(ConditionType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.name().toLowerCase());
    }

}
