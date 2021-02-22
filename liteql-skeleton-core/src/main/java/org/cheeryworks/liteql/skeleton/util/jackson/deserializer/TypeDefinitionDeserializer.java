package org.cheeryworks.liteql.skeleton.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.GraphQLTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TraitTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TypeDefinition;

import java.io.IOException;

public class TypeDefinitionDeserializer extends StdDeserializer<TypeDefinition> {

    public TypeDefinitionDeserializer() {
        super(TypeDefinition.class);
    }

    @Override
    public TypeDefinition deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode typeNode = jsonParser.readValueAsTree();

        if (typeNode.get(GraphQLTypeDefinition.EXTENSION_KEY) != null) {
            return jsonParser.getCodec().treeToValue(typeNode, GraphQLTypeDefinition.class);
        } else if (typeNode.get(TypeDefinition.TRAIT_NAME_KEY) != null
                && typeNode.get(TypeDefinition.TRAIT_NAME_KEY).asBoolean()) {
            return jsonParser.getCodec().treeToValue(typeNode, TraitTypeDefinition.class);
        } else {
            return jsonParser.getCodec().treeToValue(typeNode, DomainTypeDefinition.class);
        }
    }

}
