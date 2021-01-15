package org.cheeryworks.liteql.skeleton.util.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;
import org.cheeryworks.liteql.skeleton.util.LiteQL;

import java.io.IOException;

public class QueryTypeSerializer extends StdSerializer<QueryType> {

    public QueryTypeSerializer() {
        super(QueryType.class);
    }

    @Override
    public void serialize(QueryType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(LiteQL.StringUtils.camelNameToLowerDashConnectedLowercaseName(value.name()));
    }

}
