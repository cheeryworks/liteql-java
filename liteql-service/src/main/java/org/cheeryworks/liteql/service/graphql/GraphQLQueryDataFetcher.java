package org.cheeryworks.liteql.service.graphql;

import org.cheeryworks.liteql.model.query.QueryContext;
import org.cheeryworks.liteql.service.QueryService;
import org.cheeryworks.liteql.service.Repository;

import java.util.Map;

public class GraphQLQueryDataFetcher extends AbstractGraphQLDataFetcher {

    public GraphQLQueryDataFetcher(
            Repository repository, QueryContext queryContext, QueryService queryService,
            Map<Class, Map<String, String>> graphQLFieldReferences) {
        super(repository, queryContext, queryService, graphQLFieldReferences);
    }

}
