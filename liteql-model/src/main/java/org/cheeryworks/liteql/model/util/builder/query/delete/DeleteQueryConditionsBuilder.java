package org.cheeryworks.liteql.model.util.builder.query.delete;

import org.cheeryworks.liteql.model.query.QueryCondition;

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
