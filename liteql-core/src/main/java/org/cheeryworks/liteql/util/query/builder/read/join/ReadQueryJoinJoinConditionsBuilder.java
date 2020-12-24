package org.cheeryworks.liteql.util.query.builder.read.join;

import org.cheeryworks.liteql.query.QueryCondition;

public class ReadQueryJoinJoinConditionsBuilder extends ReadQueryJoinJoinsBuilder {

    private ReadQueryJoinMetadata readQueryJoinMetadata;

    public ReadQueryJoinJoinConditionsBuilder(ReadQueryJoinMetadata readQueryJoinMetadata) {
        super(readQueryJoinMetadata);

        this.readQueryJoinMetadata = readQueryJoinMetadata;
    }

    public ReadQueryJoinConditionsBuilder joinConditions(QueryCondition... queryConditions) {
        for (QueryCondition queryCondition : queryConditions) {
            this.readQueryJoinMetadata.getJoinConditions().add(queryCondition);
        }

        return new ReadQueryJoinConditionsBuilder(this.readQueryJoinMetadata);
    }

}
