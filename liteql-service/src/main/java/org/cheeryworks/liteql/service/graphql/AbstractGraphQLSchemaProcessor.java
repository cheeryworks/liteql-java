package org.cheeryworks.liteql.service.graphql;

import graphql.language.InputValueDefinition;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.TypeName;
import org.cheeryworks.liteql.model.util.graphql.GraphQLConstants;
import org.cheeryworks.liteql.service.GraphQLSchemaProcessor;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGraphQLSchemaProcessor implements GraphQLSchemaProcessor {

    protected List<InputValueDefinition> defaultFieldArguments() {
        List<InputValueDefinition> arguments = new ArrayList<>();

        arguments.add(
                InputValueDefinition.newInputValueDefinition()
                        .name(GraphQLConstants.QUERY_ARGUMENT_NAME_CONDITIONS)
                        .type(new ListType(
                                new NonNullType(
                                        new TypeName(GraphQLConstants.INPUT_TYPE_CONDITION_NAME))))
                        .build());

        arguments.add(
                InputValueDefinition.newInputValueDefinition()
                        .name(GraphQLConstants.QUERY_ARGUMENT_NAME_ORDER_BY)
                        .type(new ListType(
                                new NonNullType(
                                        new TypeName(GraphQLConstants.INPUT_TYPE_SORT_NAME))))
                        .build());

        return arguments;
    }

}
