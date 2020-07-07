package org.cheeryworks.liteql.model.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.TraitType;
import org.cheeryworks.liteql.model.type.Type;

import java.io.IOException;

public class TypeNameDeserializer extends StdDeserializer<Type> {

    public TypeNameDeserializer() {
        super(Type.class);
    }

    @Override
    public Type deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode typeNode = jsonParser.readValueAsTree();

        if (typeNode.isTextual()) {
            String[] typeNameParts = typeNode.asText().split("\\.");

            Type type = new Type();
            type.setSchema(typeNameParts[0]);
            type.setName(typeNameParts[1]);

            return type;
        } else if (typeNode.isObject()) {
            if (typeNode.get("trait") != null && typeNode.get("trait").asBoolean()) {
                return jsonParser.getCodec().treeToValue(typeNode, TraitType.class);
            }

            return jsonParser.getCodec().treeToValue(typeNode, DomainType.class);
        } else {
            throw new IllegalArgumentException("Type Name [" + jsonParser.getValueAsString() + "] invalid");
        }
    }

}
