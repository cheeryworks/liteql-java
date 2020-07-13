package org.cheeryworks.liteql.service;

import graphql.ExecutionResult;
import org.cheeryworks.liteql.model.query.QueryContext;

import java.util.Map;

public interface GraphQLService {

    ExecutionResult graphQL(
            QueryContext queryContext, String query);

    ExecutionResult graphQL(
            QueryContext queryContext, String query, String operationName, Map<String, Object> variables);

}
