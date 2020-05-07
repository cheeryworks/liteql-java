package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.model.enums.StandardConditionClause;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class ConditionClauseDeserializer extends StdDeserializer<StandardConditionClause> {

    public ConditionClauseDeserializer() {
        super(StandardConditionClause.class);
    }

    @Override
    public StandardConditionClause deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            StandardConditionClause conditionClause
                    = StandardConditionClause.valueOf(StringUtils.upperCase(node.asText()));

            return conditionClause;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unsupported condition clause: " + node.asText());
        }
    }

}
