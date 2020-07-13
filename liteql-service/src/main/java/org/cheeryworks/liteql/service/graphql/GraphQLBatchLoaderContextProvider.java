package org.cheeryworks.liteql.service.graphql;

import org.cheeryworks.liteql.model.query.QueryContext;
import org.dataloader.BatchLoaderContextProvider;

public class GraphQLBatchLoaderContextProvider implements BatchLoaderContextProvider {

    private GraphQLBatchLoaderContext graphQLBatchLoaderContext;

    public GraphQLBatchLoaderContextProvider(QueryContext queryContext) {
        this.graphQLBatchLoaderContext = new GraphQLBatchLoaderContext(queryContext);
    }

    @Override
    public GraphQLBatchLoaderContext getContext() {
        return graphQLBatchLoaderContext;
    }

}
