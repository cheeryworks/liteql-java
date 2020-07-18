package org.cheeryworks.liteql.service;

import graphql.schema.idl.TypeDefinitionRegistry;
import org.cheeryworks.liteql.model.graphql.Scalars;

import java.util.Map;

public interface GraphQLSchemaProcessor {

    void process(
            Repository repository, Scalars scalars, TypeDefinitionRegistry typeDefinitionRegistry,
            Map<Class, Map<String, String>> graphQLFieldReferencesWithDomainType);

}
