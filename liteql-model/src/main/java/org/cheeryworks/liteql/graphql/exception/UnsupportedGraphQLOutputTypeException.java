package org.cheeryworks.liteql.graphql.exception;

import graphql.schema.GraphQLOutputType;
import org.cheeryworks.liteql.exception.LiteQLException;

public class UnsupportedGraphQLOutputTypeException extends LiteQLException {

    public UnsupportedGraphQLOutputTypeException(GraphQLOutputType outputType) {
        super("Output type '" + outputType.toString() + "' is unsupported");
    }

}
