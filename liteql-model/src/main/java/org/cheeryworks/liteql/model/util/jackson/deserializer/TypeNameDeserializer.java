package org.cheeryworks.liteql.model.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.StructType;
import org.cheeryworks.liteql.model.type.TypeName;

import java.io.IOException;

public class TypeNameDeserializer extends StdDeserializer<TypeName> {

    public TypeNameDeserializer() {
        super(TypeName.class);
    }

    @Override
    public TypeName deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode type = jsonParser.readValueAsTree();

        if (type.isTextual()) {
            String[] domainTypeNameParts = type.asText().split("\\.");

            TypeName domainTypeName = new TypeName();
            domainTypeName.setSchema(domainTypeNameParts[0]);
            domainTypeName.setName(domainTypeNameParts[1]);

            return domainTypeName;
        } else if (type.isObject()) {
            if (type.get("struct") != null && type.get("struct").asBoolean()) {
                return jsonParser.getCodec().treeToValue(type, StructType.class);
            }

            return jsonParser.getCodec().treeToValue(type, DomainType.class);
        } else {
            throw new IllegalArgumentException("Type Name [" + jsonParser.getValueAsString() + "] invalid");
        }
    }

}
