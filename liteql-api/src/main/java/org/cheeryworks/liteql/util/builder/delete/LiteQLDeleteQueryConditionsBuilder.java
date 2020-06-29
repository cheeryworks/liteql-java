package org.cheeryworks.liteql.util.builder.delete;

import org.cheeryworks.liteql.model.query.QueryCondition;

public class LiteQLDeleteQueryConditionsBuilder {

    private LiteQLDeleteQuery liteQLDeleteQuery;

    public LiteQLDeleteQueryConditionsBuilder(LiteQLDeleteQuery liteQLDeleteQuery) {
        this.liteQLDeleteQuery = liteQLDeleteQuery;
    }

    public LiteQLDeleteQueryTruncatedBuilder conditions(QueryCondition... queryConditions) {
        for (QueryCondition queryCondition : queryConditions) {
            this.liteQLDeleteQuery.getConditions().add(queryCondition);
        }

        return new LiteQLDeleteQueryTruncatedBuilder(liteQLDeleteQuery);
    }

}
