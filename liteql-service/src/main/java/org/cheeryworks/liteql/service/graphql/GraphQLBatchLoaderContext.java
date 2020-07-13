package org.cheeryworks.liteql.service.graphql;

import org.cheeryworks.liteql.model.query.QueryContext;

public class GraphQLBatchLoaderContext {

    private QueryContext queryContext;

    public GraphQLBatchLoaderContext(QueryContext queryContext) {
        this.queryContext = queryContext;
    }

    public QueryContext getQueryContext() {
        return queryContext;
    }

}
