package org.cheeryworks.liteql.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.schema.TraitTypeDefinition;
import org.cheeryworks.liteql.schema.TypeDefinition;

import java.io.IOException;

public class TypeDefinitionDeserializer extends StdDeserializer<TypeDefinition> {

    public TypeDefinitionDeserializer() {
        super(TypeDefinition.class);
    }

    @Override
    public TypeDefinition deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode typeNode = jsonParser.readValueAsTree();

        if (typeNode.get(TypeDefinition.TRAIT_NAME_KEY) != null
                && typeNode.get(TypeDefinition.TRAIT_NAME_KEY).asBoolean()) {
            return jsonParser.getCodec().treeToValue(typeNode, TraitTypeDefinition.class);
        }

        return jsonParser.getCodec().treeToValue(typeNode, DomainTypeDefinition.class);
    }

}
