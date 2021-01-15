package org.cheeryworks.liteql.skeleton.service.graphql;

import graphql.ExecutionResult;
import org.cheeryworks.liteql.skeleton.query.QueryContext;

import java.util.Map;

public interface GraphQLService {

    ExecutionResult graphQL(
            QueryContext queryContext, String query);

    ExecutionResult graphQL(
            QueryContext queryContext, String query, String operationName, Map<String, Object> variables);

}
