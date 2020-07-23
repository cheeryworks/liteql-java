package org.cheeryworks.liteql.util.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.query.enums.ConditionOperator;

import java.io.IOException;

public class ConditionOperatorSerializer extends StdSerializer<ConditionOperator> {


    public ConditionOperatorSerializer() {
        super(ConditionOperator.class);
    }

    @Override
    public void serialize(ConditionOperator value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.name().toLowerCase());
    }

}
