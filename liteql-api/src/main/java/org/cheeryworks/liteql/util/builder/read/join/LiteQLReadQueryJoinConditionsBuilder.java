package org.cheeryworks.liteql.util.builder.read.join;

import org.cheeryworks.liteql.model.query.QueryCondition;

public class LiteQLReadQueryJoinConditionsBuilder extends LiteQLReadQueryJoinJoinsBuilder {

    private LiteQLReadQueryJoin liteQLReadQueryJoin;

    public LiteQLReadQueryJoinConditionsBuilder(LiteQLReadQueryJoin liteQLReadQueryJoin) {
        super(liteQLReadQueryJoin);

        this.liteQLReadQueryJoin = liteQLReadQueryJoin;
    }

    public LiteQLReadQueryJoinJoinsBuilder conditions(QueryCondition... queryConditions) {
        for (QueryCondition queryCondition : queryConditions) {
            this.liteQLReadQueryJoin.getConditions().add(queryCondition);
        }

        return new LiteQLReadQueryJoinJoinsBuilder(this.liteQLReadQueryJoin);
    }

}
