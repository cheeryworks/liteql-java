package org.cheeryworks.liteql.skeleton.util.query.builder.read;

import org.cheeryworks.liteql.skeleton.query.QueryCondition;

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
