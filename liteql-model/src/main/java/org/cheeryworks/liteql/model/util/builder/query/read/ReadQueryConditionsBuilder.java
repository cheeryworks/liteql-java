package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.QueryCondition;

public class ReadQueryConditionsBuilder extends ReadQuerySortsBuilder {

    public ReadQueryConditionsBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata);
    }

    public ReadQuerySortsBuilder conditions(QueryCondition... queryConditions) {
        for (QueryCondition queryCondition : queryConditions) {
            getReadQueryMetadata().getConditions().add(queryCondition);
        }

        return this;
    }

}
