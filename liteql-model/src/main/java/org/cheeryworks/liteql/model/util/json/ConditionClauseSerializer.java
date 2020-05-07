package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.model.enums.StandardConditionClause;

import java.io.IOException;

public class ConditionClauseSerializer extends StdSerializer<StandardConditionClause> {


    protected ConditionClauseSerializer() {
        super(StandardConditionClause.class);
    }

    @Override
    public void serialize(
            StandardConditionClause value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.name().toLowerCase());
    }

}
