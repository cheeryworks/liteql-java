package org.cheeryworks.liteql.model.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.enums.ConditionOperator;

import java.io.IOException;

public class ConditionOperatorDeserializer extends StdDeserializer<ConditionOperator> {

    public ConditionOperatorDeserializer() {
        super(ConditionOperator.class);
    }

    @Override
    public ConditionOperator deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            ConditionOperator conditionOperator = ConditionOperator.valueOf(StringUtils.upperCase(node.asText()));

            return conditionOperator;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unsupported condition clause: " + node.asText());
        }
    }

}
