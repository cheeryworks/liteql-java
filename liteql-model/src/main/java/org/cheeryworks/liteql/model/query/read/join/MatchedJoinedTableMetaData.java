package org.cheeryworks.liteql.model.query.read.join;

import org.cheeryworks.liteql.model.query.QueryCondition;

public class MatchedJoinedTableMetaData {

    private QueryCondition queryCondition;

    private JoinedTableMetaData joinedTableMetaData;

    public QueryCondition getQueryCondition() {
        return queryCondition;
    }

    public void setQueryCondition(QueryCondition queryCondition) {
        this.queryCondition = queryCondition;
    }

    public JoinedTableMetaData getJoinedTableMetaData() {
        return joinedTableMetaData;
    }

    public void setJoinedTableMetaData(JoinedTableMetaData joinedTableMetaData) {
        this.joinedTableMetaData = joinedTableMetaData;
    }

    public MatchedJoinedTableMetaData(QueryCondition queryCondition, JoinedTableMetaData joinedTableMetaData) {
        this.queryCondition = queryCondition;
        this.joinedTableMetaData = joinedTableMetaData;
    }

}
