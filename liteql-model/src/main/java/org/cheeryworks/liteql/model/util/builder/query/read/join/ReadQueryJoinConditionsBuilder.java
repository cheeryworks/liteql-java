package org.cheeryworks.liteql.model.util.builder.query.read.join;

import org.cheeryworks.liteql.model.query.QueryCondition;

public class ReadQueryJoinConditionsBuilder extends ReadQueryJoinJoinsBuilder {

    private ReadQueryJoinMetadata liteQLReadQueryJoin;

    public ReadQueryJoinConditionsBuilder(ReadQueryJoinMetadata liteQLReadQueryJoin) {
        super(liteQLReadQueryJoin);

        this.liteQLReadQueryJoin = liteQLReadQueryJoin;
    }

    public ReadQueryJoinJoinsBuilder conditions(QueryCondition... queryConditions) {
        for (QueryCondition queryCondition : queryConditions) {
            this.liteQLReadQueryJoin.getConditions().add(queryCondition);
        }

        return new ReadQueryJoinJoinsBuilder(this.liteQLReadQueryJoin);
    }

}
