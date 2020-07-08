package org.cheeryworks.liteql.model.util.builder.read;

import org.cheeryworks.liteql.model.query.QueryCondition;

public class LiteQLReadQueryConditionsBuilder extends LiteQLReadQuerySortsBuilder {

    private LiteQLReadQuery liteQLReadQuery;

    public LiteQLReadQueryConditionsBuilder(LiteQLReadQuery liteQLReadQuery) {
        super(liteQLReadQuery);

        this.liteQLReadQuery = liteQLReadQuery;
    }

    public LiteQLReadQuerySortsBuilder conditions(QueryCondition... queryConditions) {
        for (QueryCondition queryCondition : queryConditions) {
            this.liteQLReadQuery.getConditions().add(queryCondition);
        }

        return new LiteQLReadQuerySortsBuilder(this.liteQLReadQuery);
    }

}
