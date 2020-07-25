package org.cheeryworks.liteql.service.graphql;

import org.cheeryworks.liteql.service.query.QueryService;
import org.cheeryworks.liteql.service.schema.SchemaService;

public class GraphQLQueryDataFetcher extends AbstractGraphQLDataFetcher {

    public GraphQLQueryDataFetcher(SchemaService schemaService, QueryService queryService) {
        super(schemaService, queryService);
    }

}
