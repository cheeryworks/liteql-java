package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.model.query.condition.ConditionType;

import java.io.IOException;

public class ConditionTypeSerializer extends StdSerializer<ConditionType> {


    protected ConditionTypeSerializer() {
        super(ConditionType.class);
    }

    @Override
    public void serialize(ConditionType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getName().toLowerCase());
    }

}
