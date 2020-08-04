package org.cheeryworks.liteql.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.util.LiteQL;

import java.io.IOException;

public class TypeNameDeserializer extends StdDeserializer<TypeName> {

    public TypeNameDeserializer() {
        super(TypeName.class);
    }

    @Override
    public TypeName deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode typeNode = jsonParser.readValueAsTree();

        return LiteQL.SchemaUtils.getTypeName(typeNode.asText());
    }

}
