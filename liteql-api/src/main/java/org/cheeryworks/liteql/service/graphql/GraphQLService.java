package org.cheeryworks.liteql.service.graphql;

import graphql.ExecutionResult;
import org.cheeryworks.liteql.query.QueryContext;
import org.cheeryworks.liteql.service.LiteQLService;

import java.util.Map;

public interface GraphQLService extends LiteQLService {

    ExecutionResult graphQL(
            QueryContext queryContext, String query);

    ExecutionResult graphQL(
            QueryContext queryContext, String query, String operationName, Map<String, Object> variables);

}
