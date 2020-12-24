package org.cheeryworks.liteql.util.query.builder.read.join;

import org.cheeryworks.liteql.query.QueryConditions;
import org.cheeryworks.liteql.util.query.builder.read.AbstractReadQueryMetadata;

public class ReadQueryJoinMetadata extends AbstractReadQueryMetadata {

    private QueryConditions joinConditions = new QueryConditions();

    public QueryConditions getJoinConditions() {
        return joinConditions;
    }

    public void setJoinConditions(QueryConditions joinConditions) {
        this.joinConditions = joinConditions;
    }

}
