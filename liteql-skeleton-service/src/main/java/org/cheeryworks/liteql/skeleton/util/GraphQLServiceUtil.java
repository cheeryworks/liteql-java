package org.cheeryworks.liteql.skeleton.util;

import graphql.language.Field;
import graphql.language.Selection;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import org.cheeryworks.liteql.skeleton.query.QueryCondition;
import org.cheeryworks.liteql.skeleton.query.read.AbstractTypedReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.field.FieldDefinition;
import org.cheeryworks.liteql.skeleton.query.read.field.FieldDefinitions;
import org.cheeryworks.liteql.skeleton.query.read.sort.QuerySort;
import org.cheeryworks.liteql.skeleton.schema.TypeName;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class GraphQLServiceUtil {

    public static final String GRAPHQL_NAME_CONCAT = "__";

    public static String toObjectTypeName(TypeName typeName) {
        return typeName.getFullname().replaceAll("\\" + LiteQL.Constants.NAME_CONCAT, GRAPHQL_NAME_CONCAT);
    }

    public static TypeName toDomainTypeName(String graphQLObjectTypeName) {
        return LiteQL.SchemaUtils.getTypeName(
                graphQLObjectTypeName.replaceAll(GRAPHQL_NAME_CONCAT, LiteQL.Constants.NAME_CONCAT));
    }

    public static void parseConditions(
            AbstractTypedReadQuery readQuery, FieldDefinitions fields, DataFetchingEnvironment environment) {
        String conditions = "[]";

        if (environment.containsArgument(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_CONDITIONS)) {
            conditions = LiteQL.JacksonJsonUtils.toJson(
                    environment.getArgument(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_CONDITIONS));
        }

        QueryCondition[] queryConditions = LiteQL.JacksonJsonUtils.toBean(conditions, QueryCondition[].class);

        for (QueryCondition queryCondition : queryConditions) {
            fields.add(new FieldDefinition(queryCondition.getField()));

            readQuery.addCondition(queryCondition);
        }
    }

    public static void parseSorts(
            AbstractTypedReadQuery readQuery, FieldDefinitions fields, DataFetchingEnvironment environment) {
        String sorts = "[]";

        if (environment.containsArgument(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ORDER_BY)) {
            sorts = LiteQL.JacksonJsonUtils.toJson(
                    environment.getArgument(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ORDER_BY));
        }

        QuerySort[] querySorts = LiteQL.JacksonJsonUtils.toBean(sorts, QuerySort[].class);

        for (QuerySort querySort : querySorts) {
            fields.add(new FieldDefinition(querySort.getField()));
        }

        readQuery.setSorts(new LinkedList<>(Arrays.asList(querySorts)));
    }

    public static FieldDefinitions getFieldsFromSelections(List<Selection> selections) {
        FieldDefinitions fields = new FieldDefinitions();

        for (Selection selection : selections) {
            String fieldName = ((Field) selection).getName();

            fields.add(new FieldDefinition(fieldName));
        }

        fields.add(new FieldDefinition(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ID));

        return fields;
    }

    public static GraphQLObjectType getWrappedOutputType(GraphQLOutputType outputType) {
        GraphQLType wrappedType = null;

        if (outputType instanceof GraphQLObjectType) {
            wrappedType = outputType;
        } else if (outputType instanceof GraphQLList) {
            wrappedType = ((GraphQLList) outputType).getWrappedType();

            if (wrappedType instanceof GraphQLNonNull) {
                wrappedType = ((GraphQLNonNull) wrappedType).getWrappedType();
            }
        } else if (outputType instanceof GraphQLNonNull) {
            GraphQLNonNull nonNullType = (GraphQLNonNull) outputType;

            if (nonNullType.getWrappedType() instanceof GraphQLList) {
                GraphQLList listType = (GraphQLList) nonNullType.getWrappedType();

                wrappedType = listType.getWrappedType();

                if (wrappedType instanceof GraphQLNonNull) {
                    wrappedType = ((GraphQLNonNull) wrappedType).getWrappedType();
                }
            } else {
                wrappedType = nonNullType.getWrappedType();
            }
        }

        return (GraphQLObjectType) wrappedType;
    }

}
