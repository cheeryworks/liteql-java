package org.cheeryworks.liteql.skeleton.service.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import org.cheeryworks.liteql.skeleton.graphql.exception.UnsupportedGraphQLOutputTypeException;
import org.cheeryworks.liteql.skeleton.query.AuditQueryContext;
import org.cheeryworks.liteql.skeleton.query.DomainQuery;
import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.QueryContext;
import org.cheeryworks.liteql.skeleton.query.enums.ConditionClause;
import org.cheeryworks.liteql.skeleton.query.enums.ConditionType;
import org.cheeryworks.liteql.skeleton.query.read.PageReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.ReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.SingleReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.field.FieldDefinitions;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResult;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResults;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResultsData;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.service.query.QueryAccessDecisionService;
import org.cheeryworks.liteql.skeleton.service.query.QueryService;
import org.cheeryworks.liteql.skeleton.service.schema.SchemaService;
import org.cheeryworks.liteql.skeleton.util.GraphQLServiceUtil;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.cheeryworks.liteql.skeleton.util.query.builder.QueryBuilder;
import org.dataloader.DataLoader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.cheeryworks.liteql.skeleton.util.query.builder.QueryBuilderUtil.condition;
import static org.cheeryworks.liteql.skeleton.util.query.builder.QueryBuilderUtil.field;

public abstract class AbstractGraphQLDataFetcher implements DataFetcher {

    private SchemaService schemaService;

    private QueryService queryService;

    private QueryAccessDecisionService queryAccessDecisionService;

    public AbstractGraphQLDataFetcher(
            SchemaService schemaService, QueryService queryService,
            QueryAccessDecisionService queryAccessDecisionService) {
        this.schemaService = schemaService;
        this.queryService = queryService;
        this.queryAccessDecisionService = queryAccessDecisionService;
    }

    protected SchemaService getSchemaService() {
        return this.schemaService;
    }

    protected QueryService getQueryService() {
        return this.queryService;
    }

    protected QueryAccessDecisionService getQueryAccessDecisionService() {
        return this.queryAccessDecisionService;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        QueryContext queryContext = environment.getContext();

        Object data = null;

        if (environment.getSource() == null) {
            if (isListOutputType(environment.getFieldType())) {
                data = getByConditions(queryContext, environment);
            } else if (environment.getArgument(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ID) != null) {
                data = getById(queryContext, environment);
            } else {
                throw new UnsupportedGraphQLOutputTypeException(environment.getFieldType());
            }
        } else {
            Map<String, Object> source = environment.getSource();

            GraphQLObjectType outputType = GraphQLServiceUtil.getWrappedOutputType(environment.getFieldType());

            DataLoader defaultDataLoader = environment.getDataLoader(
                    LiteQL.Constants.GraphQL.QUERY_DEFAULT_DATA_LOADER_KEY);

            Map<String, Object> keyContext = new HashMap<>();
            keyContext.put(LiteQL.Constants.GraphQL.QUERY_DOMAIN_TYPE_NAME_KEY, outputType.getName());
            keyContext.put(LiteQL.Constants.GraphQL.QUERY_DATA_FETCHING_ENVIRONMENT_KEY, environment);

            String parentFieldName = environment.getField().getName();

            if (isListOutputType(environment.getFieldType())) {
                Map<String, Object> childrenContext = getChildrenContext(
                        queryContext,
                        source.get(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ID).toString(),
                        environment.getField().getName(),
                        outputType.getName(), keyContext);
                data = defaultDataLoader.loadMany(
                        Arrays.asList(childrenContext.keySet().toArray()),
                        Arrays.asList(childrenContext.values().toArray()));
            } else if (source.containsKey(parentFieldName)) {
                if (source.get(parentFieldName) != null) {
                    data = defaultDataLoader.load(source.get(parentFieldName), keyContext);
                }
            } else {
                throw new UnsupportedGraphQLOutputTypeException(environment.getFieldType());
            }
        }

        return data;
    }

    private boolean isListOutputType(GraphQLOutputType outputType) {
        if (outputType instanceof GraphQLList) {
            return true;
        }

        if (outputType instanceof GraphQLNonNull
                && ((GraphQLNonNull) outputType).getWrappedType() instanceof GraphQLList) {
            return true;
        }

        return false;
    }

    private Object getById(QueryContext queryContext, DataFetchingEnvironment environment) {
        GraphQLObjectType outputType = GraphQLServiceUtil.getWrappedOutputType(environment.getFieldType());

        TypeName domainTypeName = GraphQLServiceUtil.toDomainTypeName(outputType.getName());

        SingleReadQuery singleReadQuery = QueryBuilder
                .read(domainTypeName)
                .fields()
                .single()
                .getQuery();

        FieldDefinitions fields = GraphQLServiceUtil.getFieldsFromSelections(
                outputType, environment.getField().getSelectionSet().getSelections());

        singleReadQuery.addCondition(
                LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ID, ConditionClause.EQUALS, ConditionType.String,
                environment.getArgument(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ID));

        singleReadQuery.setFields(fields);

        if (queryContext instanceof AuditQueryContext) {
            getQueryAccessDecisionService().decide(((AuditQueryContext) queryContext).getUser(), singleReadQuery);
        }

        return queryService.read(queryContext, singleReadQuery);
    }

    private List<ReadResult> getByConditions(QueryContext queryContext, DataFetchingEnvironment environment) {
        GraphQLObjectType outputType = GraphQLServiceUtil.getWrappedOutputType(environment.getFieldType());

        TypeName domainTypeName = GraphQLServiceUtil.toDomainTypeName(outputType.getName());

        ReadQuery readQuery = QueryBuilder
                .read(domainTypeName)
                .fields()
                .getQuery();

        FieldDefinitions fields = GraphQLServiceUtil.getFieldsFromSelections(
                outputType, environment.getField().getSelectionSet().getSelections());

        GraphQLServiceUtil.parseConditions(readQuery, fields, environment);

        GraphQLServiceUtil.parseSorts(readQuery, fields, environment);

        readQuery.setFields(fields);

        Integer offset = null;
        if (environment.containsArgument(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_PAGINATION_OFFSET)) {
            offset = environment.getArgument(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_PAGINATION_OFFSET);
        }

        Integer first = null;
        if (environment.containsArgument(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_PAGINATION_FIRST)) {
            first = environment.getArgument(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_PAGINATION_FIRST);
        }

        PublicQuery query = readQuery;

        if (offset != null && first != null) {
            query = new PageReadQuery(readQuery, offset / first, first);
        }

        if (queryContext instanceof AuditQueryContext) {
            getQueryAccessDecisionService().decide(((AuditQueryContext) queryContext).getUser(), (DomainQuery) query);
        }

        return ((ReadResultsData<ReadResult>) queryService.execute(queryContext, query)).getData();
    }

    private Map<String, Object> getChildrenContext(
            QueryContext queryContext, String parentId, String fieldName,
            String childTypeName, Map<String, Object> keyContext) {
        Map<String, Object> childrenContext = new LinkedHashMap<>();

        TypeName domainTypeName = GraphQLServiceUtil.toDomainTypeName(childTypeName);

        String parentFieldName = fieldName;

        ReadQuery readQuery = QueryBuilder
                .read(domainTypeName)
                .fields(
                        field(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ID),
                        field(parentFieldName)
                )
                .conditions(
                        condition(
                                parentFieldName,
                                ConditionClause.EQUALS, ConditionType.String, parentId)
                )
                .getQuery();

        DataFetchingEnvironment dataFetchingEnvironment =
                (DataFetchingEnvironment) keyContext.get(
                        LiteQL.Constants.GraphQL.QUERY_DATA_FETCHING_ENVIRONMENT_KEY);

        FieldDefinitions fields = readQuery.getFields();

        GraphQLServiceUtil.parseConditions(readQuery, fields, dataFetchingEnvironment);

        GraphQLServiceUtil.parseSorts(readQuery, fields, dataFetchingEnvironment);

        readQuery.setFields(fields);

        if (queryContext instanceof AuditQueryContext) {
            getQueryAccessDecisionService().decide(((AuditQueryContext) queryContext).getUser(), readQuery);
        }

        ReadResults dataSet = queryService.read(queryContext, readQuery);

        for (Map<String, Object> data : dataSet) {
            childrenContext.put(data.get(LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ID).toString(), keyContext);
        }

        return childrenContext;
    }

}
