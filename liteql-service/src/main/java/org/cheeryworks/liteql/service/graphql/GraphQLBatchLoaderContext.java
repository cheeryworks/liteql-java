package org.cheeryworks.liteql.service.graphql;

import org.cheeryworks.liteql.service.QueryService;

public class GraphQLBatchLoaderContext {

    private QueryService queryService;

    public GraphQLBatchLoaderContext(QueryService queryService) {
        this.queryService = queryService;
    }

    public QueryService getQueryService() {
        return this.queryService;
    }

}
