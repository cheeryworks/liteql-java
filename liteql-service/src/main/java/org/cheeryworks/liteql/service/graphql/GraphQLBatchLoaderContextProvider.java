package org.cheeryworks.liteql.service.graphql;

import org.cheeryworks.liteql.service.QueryService;
import org.dataloader.BatchLoaderContextProvider;

public class GraphQLBatchLoaderContextProvider implements BatchLoaderContextProvider {

    private GraphQLBatchLoaderContext graphQLBatchLoaderContext;

    public GraphQLBatchLoaderContextProvider(QueryService queryService) {
        this.graphQLBatchLoaderContext = new GraphQLBatchLoaderContext(queryService);
    }

    @Override
    public GraphQLBatchLoaderContext getContext() {
        return graphQLBatchLoaderContext;
    }

}
