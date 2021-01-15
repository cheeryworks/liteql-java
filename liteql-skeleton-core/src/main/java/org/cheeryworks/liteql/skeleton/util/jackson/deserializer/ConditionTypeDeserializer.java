package org.cheeryworks.liteql.skeleton.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.skeleton.query.enums.ConditionType;

import java.io.IOException;

public class ConditionTypeDeserializer extends StdDeserializer<ConditionType> {

    public ConditionTypeDeserializer() {
        super(ConditionType.class);
    }

    @Override
    public ConditionType deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String conditionTypeInString = jsonParser.getValueAsString();

        if (StringUtils.isNotBlank(conditionTypeInString)) {
            for (ConditionType conditionType : ConditionType.values()) {
                if (conditionType.name().toLowerCase().equals(conditionTypeInString.toLowerCase())) {
                    return conditionType;
                }
            }

            throw new IllegalArgumentException(
                    "Unsupported condition type: " + conditionTypeInString);

        } else {
            throw new IllegalArgumentException("Condition type not specified");
        }
    }

}
