package org.cheeryworks.liteql.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.Field;
import graphql.language.Selection;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchemaElement;
import graphql.schema.GraphQLType;
import org.cheeryworks.liteql.model.annotation.ResourceDefinition;
import org.cheeryworks.liteql.model.query.QueryCondition;
import org.cheeryworks.liteql.model.query.read.AbstractTypedReadQuery;
import org.cheeryworks.liteql.model.query.read.field.FieldDefinition;
import org.cheeryworks.liteql.model.query.read.field.FieldDefinitions;
import org.cheeryworks.liteql.model.query.read.sort.QuerySort;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.cheeryworks.liteql.model.util.StringUtil;
import org.cheeryworks.liteql.model.util.graphql.GraphQLConstants;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class GraphQLServiceUtil {

    public static final String GRAPHQL_NAME_CONCAT = "__";

    public static String getObjectTypeName(TypeName typeName) {
        return typeName.getFullname().replaceAll("\\" + LiteQLConstants.NAME_CONCAT, GRAPHQL_NAME_CONCAT);
    }

    public static String getObjectTypeName(Class<?> domainType) {
        ResourceDefinition resourceDefinition = domainType.getAnnotation(ResourceDefinition.class);

        return resourceDefinition.namespace().toLowerCase()
                .replaceAll("\\" + LiteQLConstants.NAME_CONCAT, GRAPHQL_NAME_CONCAT)
                + GRAPHQL_NAME_CONCAT
                + StringUtil.camelNameToLowerDashConnectedLowercaseName(domainType.getSimpleName());
    }

    public static String normalizeGraphQLFieldName(String graphQLFieldName) {
        return graphQLFieldName.replaceAll(GRAPHQL_NAME_CONCAT, LiteQLConstants.NAME_CONCAT);
    }

    public static void parseConditions(
            AbstractTypedReadQuery readQuery, FieldDefinitions fields,
            DataFetchingEnvironment environment, ObjectMapper objectMapper) {
        String conditions = "[]";

        if (environment.containsArgument(GraphQLConstants.QUERY_ARGUMENT_NAME_CONDITIONS)) {
            conditions = LiteQLJsonUtil.toJson(
                    objectMapper,
                    environment.getArgument(GraphQLConstants.QUERY_ARGUMENT_NAME_CONDITIONS));
        }

        QueryCondition[] queryConditions = LiteQLJsonUtil.toBean(objectMapper, conditions, QueryCondition[].class);

        for (QueryCondition queryCondition : queryConditions) {
            fields.add(new FieldDefinition(queryCondition.getField()));

            readQuery.addCondition(queryCondition);
        }
    }

    public static void parseSorts(
            AbstractTypedReadQuery readQuery, FieldDefinitions fields,
            DataFetchingEnvironment environment, ObjectMapper objectMapper) {
        String sorts = "[]";

        if (environment.containsArgument(GraphQLConstants.QUERY_ARGUMENT_NAME_ORDER_BY)) {
            sorts = LiteQLJsonUtil.toJson(
                    objectMapper,
                    environment.getArgument(GraphQLConstants.QUERY_ARGUMENT_NAME_ORDER_BY));
        }

        QuerySort[] querySorts = LiteQLJsonUtil.toBean(objectMapper, sorts, QuerySort[].class);

        for (QuerySort querySort : querySorts) {
            fields.add(new FieldDefinition(querySort.getField()));
        }

        readQuery.setSorts(new LinkedList<>(Arrays.asList(querySorts)));
    }

    public static FieldDefinitions getFieldsFromSelections(GraphQLObjectType outputType, List<Selection> selections) {
        FieldDefinitions fields = new FieldDefinitions();

        for (Selection selection : selections) {
            String fieldName = ((Field) selection).getName();
            if (selection.getChildren() == null || selection.getChildren().size() == 0) {
                fields.add(new FieldDefinition(fieldName));
            } else if (!isListField(outputType.getChildren(), fieldName)) {
                fields.add(new FieldDefinition(fieldName + "Id"));
            }
        }

        fields.add(new FieldDefinition(GraphQLConstants.QUERY_ARGUMENT_NAME_ID));

        return fields;
    }

    private static boolean isListField(List<GraphQLSchemaElement> fieldTypes, String fieldName) {
        for (GraphQLSchemaElement fieldType : fieldTypes) {
            if (((GraphQLFieldDefinition) fieldType).getName().equals(fieldName)) {
                return fieldType.getChildren() != null
                        && fieldType.getChildren().size() > 0
                        && fieldType.getChildren().get(0) instanceof GraphQLList;
            }
        }

        return false;
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
