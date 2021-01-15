package org.cheeryworks.liteql.skeleton.graphql.exception;

import graphql.schema.GraphQLOutputType;
import org.cheeryworks.liteql.skeleton.exception.LiteQLException;

public class UnsupportedGraphQLOutputTypeException extends LiteQLException {

    public UnsupportedGraphQLOutputTypeException(GraphQLOutputType outputType) {
        super("Output type '" + outputType.toString() + "' is unsupported");
    }

}
