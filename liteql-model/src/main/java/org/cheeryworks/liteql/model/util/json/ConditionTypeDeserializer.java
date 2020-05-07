package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.model.query.condition.ConditionType;
import org.cheeryworks.liteql.model.util.ConditionTypeUtil;

import java.io.IOException;

public class ConditionTypeDeserializer extends StdDeserializer<ConditionType> {

    public ConditionTypeDeserializer() {
        super(ConditionType.class);
    }

    @Override
    public ConditionType deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        return ConditionTypeUtil.getConditionTypeByName(node.asText().toLowerCase());
    }

}
