package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.model.enums.ConditionClause;

import java.io.IOException;

public class ConditionClauseSerializer extends StdSerializer<ConditionClause> {

    protected ConditionClauseSerializer() {
        super(ConditionClause.class);
    }

    @Override
    public void serialize(
            ConditionClause value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.name().toLowerCase());
    }

}
