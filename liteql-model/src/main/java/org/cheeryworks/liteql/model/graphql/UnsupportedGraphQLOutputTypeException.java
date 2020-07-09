package org.cheeryworks.liteql.model.graphql;

import graphql.GraphQLException;
import graphql.schema.GraphQLOutputType;

public class UnsupportedGraphQLOutputTypeException extends GraphQLException {

    public UnsupportedGraphQLOutputTypeException(GraphQLOutputType outputType) {
        super("Output type '" + outputType.toString() + "' is unsupported");
    }

}
