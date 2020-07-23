package org.cheeryworks.liteql.service.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cheeryworks.liteql.service.query.QueryService;
import org.cheeryworks.liteql.service.schema.SchemaService;

public class GraphQLQueryDataFetcher extends AbstractGraphQLDataFetcher {

    public GraphQLQueryDataFetcher(
            SchemaService schemaService, ObjectMapper objectMapper, QueryService queryService) {
        super(schemaService, objectMapper, queryService);
    }

}
