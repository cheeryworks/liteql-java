package org.cheeryworks.liteql.skeleton.util.query.builder.read.join;

import org.cheeryworks.liteql.skeleton.query.QueryCondition;

public class ReadQueryJoinConditionsBuilder extends ReadQueryJoinJoinsBuilder {

    private ReadQueryJoinMetadata readQueryJoinMetadata;

    public ReadQueryJoinConditionsBuilder(ReadQueryJoinMetadata readQueryJoinMetadata) {
        super(readQueryJoinMetadata);

        this.readQueryJoinMetadata = readQueryJoinMetadata;
    }

    public ReadQueryJoinJoinsBuilder conditions(QueryCondition... queryConditions) {
        for (QueryCondition queryCondition : queryConditions) {
            this.readQueryJoinMetadata.getConditions().add(queryCondition);
        }

        return new ReadQueryJoinJoinsBuilder(this.readQueryJoinMetadata);
    }

}
