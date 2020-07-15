package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.QueryCondition;

public class ReadQueryConditionsBuilder extends ReadQuerySortsBuilder {

    private ReadQueryMetadata readQueryMetadata;

    public ReadQueryConditionsBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata);

        this.readQueryMetadata = readQueryMetadata;
    }

    public ReadQuerySortsBuilder conditions(QueryCondition... queryConditions) {
        for (QueryCondition queryCondition : queryConditions) {
            this.readQueryMetadata.getConditions().add(queryCondition);
        }

        return new ReadQuerySortsBuilder(this.readQueryMetadata);
    }

}
