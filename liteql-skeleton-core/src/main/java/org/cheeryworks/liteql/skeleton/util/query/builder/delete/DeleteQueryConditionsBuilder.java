package org.cheeryworks.liteql.skeleton.util.query.builder.delete;

import org.cheeryworks.liteql.skeleton.query.QueryCondition;

public class DeleteQueryConditionsBuilder {

    private DeleteQueryMetadata deleteQueryMetadata;

    public DeleteQueryConditionsBuilder(DeleteQueryMetadata deleteQueryMetadata) {
        this.deleteQueryMetadata = deleteQueryMetadata;
    }

    public DeleteQueryTruncatedBuilder conditions(QueryCondition... queryConditions) {
        for (QueryCondition queryCondition : queryConditions) {
            this.deleteQueryMetadata.getConditions().add(queryCondition);
        }

        return new DeleteQueryTruncatedBuilder(deleteQueryMetadata);
    }

}
