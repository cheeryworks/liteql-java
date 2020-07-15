package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.QueryCondition;

public class ReadQueryConditionsBuilder extends ReadQuerySortsBuilder {

    private ReadQueryMetadata liteQLReadQuery;

    public ReadQueryConditionsBuilder(ReadQueryMetadata liteQLReadQuery) {
        super(liteQLReadQuery);

        this.liteQLReadQuery = liteQLReadQuery;
    }

    public ReadQuerySortsBuilder conditions(QueryCondition... queryConditions) {
        for (QueryCondition queryCondition : queryConditions) {
            this.liteQLReadQuery.getConditions().add(queryCondition);
        }

        return new ReadQuerySortsBuilder(this.liteQLReadQuery);
    }

}
