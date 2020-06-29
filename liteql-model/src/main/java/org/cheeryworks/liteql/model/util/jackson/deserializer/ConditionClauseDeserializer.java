package org.cheeryworks.liteql.model.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.enums.ConditionClause;

import java.io.IOException;

public class ConditionClauseDeserializer extends StdDeserializer<ConditionClause> {

    public ConditionClauseDeserializer() {
        super(ConditionClause.class);
    }

    @Override
    public ConditionClause deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            ConditionClause conditionClause
                    = ConditionClause.valueOf(StringUtils.upperCase(node.asText()));

            return conditionClause;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unsupported condition clause: " + node.asText());
        }
    }

}
