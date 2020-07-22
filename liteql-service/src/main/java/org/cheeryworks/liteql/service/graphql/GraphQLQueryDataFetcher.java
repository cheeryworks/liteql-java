package org.cheeryworks.liteql.service.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.service.query.QueryService;
import org.cheeryworks.liteql.service.repository.Repository;

public class GraphQLQueryDataFetcher extends AbstractGraphQLDataFetcher {

    public GraphQLQueryDataFetcher(
            Repository repository, ObjectMapper objectMapper, QueryService queryService) {
        super(repository, objectMapper, queryService);
    }

}
