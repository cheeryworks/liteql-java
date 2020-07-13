package org.cheeryworks.liteql.service.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.service.QueryService;
import org.cheeryworks.liteql.service.Repository;

import java.util.Map;

public class GraphQLQueryDataFetcher extends AbstractGraphQLDataFetcher {

    public GraphQLQueryDataFetcher(
            Repository repository, ObjectMapper objectMapper, QueryService queryService,
            Map<Class, Map<String, String>> graphQLFieldReferences) {
        super(repository, objectMapper, queryService, graphQLFieldReferences);
    }

}
