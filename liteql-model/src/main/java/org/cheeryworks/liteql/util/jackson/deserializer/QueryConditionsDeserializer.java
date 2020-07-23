package org.cheeryworks.liteql.util.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.query.QueryCondition;
import org.cheeryworks.liteql.query.QueryConditions;
import org.cheeryworks.liteql.util.LiteQLUtil;

import java.io.IOException;

public class QueryConditionsDeserializer extends StdDeserializer<QueryConditions> {

    public QueryConditionsDeserializer() {
        super(QueryCondition.class);
    }

    @Override
    public QueryConditions deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        QueryConditions queryConditions = new QueryConditions();

        for (JsonNode queryConditionNode : node) {
            QueryCondition queryCondition = jsonParser.getCodec().treeToValue(
                    queryConditionNode, QueryCondition.class);

            if (queryCondition.getType() == null) {
                queryCondition.setType(LiteQLUtil.getConditionTypeByValue(queryCondition.getValue()));
            }

            queryConditions.add(queryCondition);
        }

        return queryConditions;
    }

}
