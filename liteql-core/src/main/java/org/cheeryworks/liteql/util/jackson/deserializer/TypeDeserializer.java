package org.cheeryworks.liteql.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.Trait;
import org.cheeryworks.liteql.schema.TraitType;
import org.cheeryworks.liteql.schema.Type;

import java.io.IOException;

public class TypeDeserializer extends StdDeserializer<Type> {

    public TypeDeserializer() {
        super(Type.class);
    }

    @Override
    public Type deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode typeNode = jsonParser.readValueAsTree();

        if (typeNode.get(Trait.class.getSimpleName().toLowerCase()) != null
                && typeNode.get(Trait.class.getSimpleName().toLowerCase()).asBoolean()) {
            return jsonParser.getCodec().treeToValue(typeNode, TraitType.class);
        }

        return jsonParser.getCodec().treeToValue(typeNode, DomainType.class);
    }

}
