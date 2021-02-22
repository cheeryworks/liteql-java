package org.cheeryworks.liteql.skeleton.util;

import graphql.language.Field;
import graphql.language.Selection;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
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

    public static TypeName graphQLTypeNameToDomainTypeName(String graphQLObjectTypeName) {
        return LiteQL.SchemaUtils.getTypeName(
                graphQLObjectTypeName.replaceAll(GRAPHQL_NAME_CONCAT, LiteQL.Constants.NAME_CONCAT));
    }

    public static void fillReadQueryWithDataFetchingEnvironment(
            AbstractTypedReadQuery readQuery, GraphQLObjectType outputType,
            DataFetchingEnvironment dataFetchingEnvironment) {
        parseFields(readQuery, outputType, dataFetchingEnvironment);

        parseConditions(readQuery, dataFetchingEnvironment);

        parseSorts(readQuery, dataFetchingEnvironment);
    }

    public static void parseFields(
            AbstractTypedReadQuery readQuery, GraphQLObjectType outputType,
            DataFetchingEnvironment dataFetchingEnvironment) {
        FieldDefinitions fields = new FieldDefinitions();

        for (Selection selection : dataFetchingEnvironment.getField().getSelectionSet().getSelections()) {
            String fieldName = ((Field) selection).getName();

            if (isListField(outputType.getFieldDefinitions(), fieldName)) {
                continue;
            }

            fields.add(new FieldDefinition(fieldName));
        }

        fields.add(new FieldDefinition(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ID));

        readQuery.setFields(fields);
    }

    private static boolean isListField(List<GraphQLFieldDefinition> fieldDefinitions, String fieldName) {
        for (GraphQLFieldDefinition fieldDefinition : fieldDefinitions) {
            if (fieldDefinition.getName().equals(fieldName) && fieldDefinition.getType() instanceof GraphQLList) {
                return true;
            }
        }

        return false;
    }

    public static void parseConditions(
            AbstractTypedReadQuery readQuery, DataFetchingEnvironment dataFetchingEnvironment) {
        String conditions = "[]";

        if (dataFetchingEnvironment.containsArgument(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_CONDITIONS)) {
            conditions = LiteQL.JacksonJsonUtils.toJson(
                    dataFetchingEnvironment.getArgument(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_CONDITIONS));
        }

        QueryCondition[] queryConditions = LiteQL.JacksonJsonUtils.toBean(conditions, QueryCondition[].class);

        for (QueryCondition queryCondition : queryConditions) {
            readQuery.addCondition(queryCondition);
        }
    }

    public static void parseSorts(
            AbstractTypedReadQuery readQuery, DataFetchingEnvironment dataFetchingEnvironment) {
        String sorts = "[]";

        if (dataFetchingEnvironment.containsArgument(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ORDER_BY)) {
            sorts = LiteQL.JacksonJsonUtils.toJson(
                    dataFetchingEnvironment.getArgument(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ORDER_BY));
        }

        QuerySort[] querySorts = LiteQL.JacksonJsonUtils.toBean(sorts, QuerySort[].class);

        readQuery.setSorts(new LinkedList<>(Arrays.asList(querySorts)));
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
