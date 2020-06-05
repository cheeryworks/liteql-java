package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.enums.ConditionClause;
import org.cheeryworks.liteql.model.enums.MigrationOperationType;

import java.io.IOException;

public class MigrationOperationTypeDeserializer extends StdDeserializer<MigrationOperationType> {

    public MigrationOperationTypeDeserializer() {
        super(ConditionClause.class);
    }

    @Override
    public MigrationOperationType deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            MigrationOperationType migrationOperationType
                    = MigrationOperationType.valueOf(StringUtils.upperCase(node.asText()));

            return migrationOperationType;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unsupported migration operation type: " + node.asText());
        }
    }

}
