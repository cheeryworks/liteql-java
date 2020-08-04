package org.cheeryworks.liteql.util.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cheeryworks.liteql.schema.TypeName;

import java.io.IOException;

public class TypeNameSerializer extends StdSerializer<TypeName> {

    public TypeNameSerializer() {
        super(TypeName.class);
    }

    @Override
    public void serialize(TypeName value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getFullname());
    }

}
