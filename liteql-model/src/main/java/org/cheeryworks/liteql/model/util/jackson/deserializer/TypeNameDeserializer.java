package org.cheeryworks.liteql.model.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.TraitType;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.util.LiteQLUtil;

import java.io.IOException;

public class TypeNameDeserializer extends StdDeserializer<TypeName> {

    public TypeNameDeserializer() {
        super(TypeName.class);
    }

    @Override
    public TypeName deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode typeNode = jsonParser.readValueAsTree();

        if (typeNode.isTextual()) {
            return LiteQLUtil.getTypeName(typeNode.asText());
        } else if (typeNode.isObject()) {
            if (typeNode.get("trait") != null && typeNode.get("trait").asBoolean()) {
                return jsonParser.getCodec().treeToValue(typeNode, TraitType.class);
            }

            return jsonParser.getCodec().treeToValue(typeNode, DomainType.class);
        } else {
            throw new IllegalArgumentException("TypeName Name [" + jsonParser.getValueAsString() + "] invalid");
        }
    }

}
