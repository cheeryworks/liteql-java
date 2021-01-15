package org.cheeryworks.liteql.skeleton.util.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.skeleton.query.enums.ConditionClause;

import java.io.IOException;

public class ConditionClauseSerializer extends StdSerializer<ConditionClause> {


    public ConditionClauseSerializer() {
        super(ConditionClause.class);
    }

    @Override
    public void serialize(
            ConditionClause value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.name().toLowerCase());
    }

}
