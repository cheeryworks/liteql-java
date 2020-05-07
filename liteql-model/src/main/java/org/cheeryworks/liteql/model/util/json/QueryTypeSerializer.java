package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.model.enums.QueryType;

import java.io.IOException;

public class QueryTypeSerializer extends StdSerializer<QueryType> {


    protected QueryTypeSerializer() {
        super(QueryType.class);
    }

    @Override
    public void serialize(QueryType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.name().toLowerCase());
    }

}
