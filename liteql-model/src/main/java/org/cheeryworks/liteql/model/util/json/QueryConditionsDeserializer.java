package org.cheeryworks.liteql.model.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.cheeryworks.liteql.model.query.QueryCondition;
import org.cheeryworks.liteql.model.query.QueryConditions;
import org.cheeryworks.liteql.model.util.ConditionTypeUtil;

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
            QueryCondition queryCondition = LiteQLJsonUtil.toBean(
                    queryConditionNode.toString(), QueryCondition.class);

            if (queryCondition.getType() == null) {
                queryCondition.setType(ConditionTypeUtil.getConditionTypeByValue(queryCondition.getValue()));
            }

            queryConditions.add(queryCondition);
        }

        return queryConditions;
    }

}
