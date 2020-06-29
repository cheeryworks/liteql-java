package org.cheeryworks.liteql.model.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.cheeryworks.liteql.model.query.PublicQuery;
import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.query.save.SaveQueryAssociations;

import java.io.IOException;

public class SaveQueryAssociationsDeserializer extends StdDeserializer<SaveQueryAssociations> {

    public SaveQueryAssociationsDeserializer() {
        super(SaveQueryAssociations.class);
    }

    @Override
    public SaveQueryAssociations deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        SaveQueryAssociations saveQueryAssociations = new SaveQueryAssociations();

        JsonNode saveAssociationsNode = jsonParser.getCodec().readTree(jsonParser);

        if (saveAssociationsNode != null) {
            if (saveAssociationsNode instanceof ArrayNode) {
                for (JsonNode field : saveAssociationsNode) {
                    PublicQuery query = jsonParser.getCodec().treeToValue(field, PublicQuery.class);

                    saveQueryAssociations.add((AbstractSaveQuery) query);
                }
            } else {
                throw new IllegalArgumentException(
                        "Save associations not supported: \n" + saveAssociationsNode.asText());
            }
        }

        return saveQueryAssociations;
    }

}
