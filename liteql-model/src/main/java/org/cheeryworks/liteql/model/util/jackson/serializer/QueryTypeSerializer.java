package org.cheeryworks.liteql.model.util.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.util.StringUtil;

import java.io.IOException;

public class QueryTypeSerializer extends StdSerializer<QueryType> {

    public QueryTypeSerializer() {
        super(QueryType.class);
    }

    @Override
    public void serialize(QueryType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(StringUtil.camelNameToLowerDashConnectedLowercaseName(value.name()));
    }

}