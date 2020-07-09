package org.cheeryworks.liteql.service;

import graphql.ExecutionResult;

import java.util.Map;

public interface GraphQLService {

    ExecutionResult graphQL(String query);

    ExecutionResult graphQL(String query, String operationName, Map<String, Object> variables);

}
