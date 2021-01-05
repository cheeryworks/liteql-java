package org.cheeryworks.liteql.util.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.query.enums.QueryPhase;
import org.cheeryworks.liteql.util.LiteQL;

import java.io.IOException;

public class QueryPhaseSerializer extends StdSerializer<QueryPhase> {

    public QueryPhaseSerializer() {
        super(QueryPhase.class);
    }

    @Override
    public void serialize(QueryPhase value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(LiteQL.StringUtils.camelNameToLowerDashConnectedLowercaseName(value.name()));
    }

}
