package org.cheeryworks.liteql.service.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.query.enums.ConditionClause;
import org.cheeryworks.liteql.query.enums.ConditionType;
import org.cheeryworks.liteql.graphql.exception.UnsupportedGraphQLOutputTypeException;
import org.cheeryworks.liteql.query.PublicQuery;
import org.cheeryworks.liteql.query.QueryContext;
import org.cheeryworks.liteql.query.read.PageReadQuery;
import org.cheeryworks.liteql.query.read.ReadQuery;
import org.cheeryworks.liteql.query.read.SingleReadQuery;
import org.cheeryworks.liteql.query.read.field.FieldDefinitions;
import org.cheeryworks.liteql.query.read.result.ReadResult;
import org.cheeryworks.liteql.query.read.result.ReadResults;
import org.cheeryworks.liteql.query.read.result.ReadResultsData;
import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.field.ReferenceField;
import org.cheeryworks.liteql.util.query.builder.QueryBuilder;
import org.cheeryworks.liteql.util.graphql.GraphQLConstants;
import org.cheeryworks.liteql.service.query.QueryService;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.util.GraphQLServiceUtil;
import org.dataloader.DataLoader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.cheeryworks.liteql.util.query.builder.QueryBuilderUtil.condition;
import static org.cheeryworks.liteql.util.query.builder.QueryBuilderUtil.field;

public abstract class AbstractGraphQLDataFetcher implements DataFetcher {

    private SchemaService schemaService;

    private ObjectMapper objectMapper;

    private QueryService queryService;

    public AbstractGraphQLDataFetcher(
            SchemaService schemaService, ObjectMapper objectMapper, QueryService queryService) {
        this.schemaService = schemaService;
        this.objectMapper = objectMapper;
        this.queryService = queryService;
    }

    protected SchemaService getSchemaService() {
        return this.schemaService;
    }

    protected QueryService getQueryService() {
        return this.queryService;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        QueryContext queryContext = environment.getContext();

        Object data = null;

        if (environment.getSource() == null) {
            if (isListOutputType(environment.getFieldType())) {
                data = getByConditions(queryContext, environment);
            } else if (environment.getArgument(GraphQLConstants.QUERY_ARGUMENT_NAME_ID) != null) {
                data = getById(queryContext, environment);
            } else {
                throw new UnsupportedGraphQLOutputTypeException(environment.getFieldType());
            }
        } else {
            Map<String, Object> source = environment.getSource();

            GraphQLObjectType outputType = GraphQLServiceUtil.getWrappedOutputType(environment.getFieldType());

            DataLoader defaultDataLoader = environment.getDataLoader(
                    GraphQLConstants.QUERY_DEFAULT_DATA_LOADER_KEY);

            Map<String, Object> keyContext = new HashMap<>();
            keyContext.put(GraphQLConstants.QUERY_DOMAIN_TYPE_NAME_KEY, outputType.getName());
            keyContext.put(GraphQLConstants.QUERY_DATA_FETCHING_ENVIRONMENT_KEY, environment);

            String parentFieldName = getParentFieldName(
                    environment.getField().getName(),
                    ((GraphQLObjectType) environment.getParentType()).getName());

            if (isListOutputType(environment.getFieldType())) {
                Map<String, Object> childrenContext = getChildrenContext(
                        queryContext,
                        source.get(GraphQLConstants.QUERY_ARGUMENT_NAME_ID).toString(),
                        environment.getField().getName(),
                        ((GraphQLObjectType) environment.getParentType()).getName(),
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
                GraphQLConstants.QUERY_ARGUMENT_NAME_ID, ConditionClause.EQUALS, ConditionType.String,
                environment.getArgument(GraphQLConstants.QUERY_ARGUMENT_NAME_ID));

        singleReadQuery.setFields(fields);

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

        GraphQLServiceUtil.parseConditions(
                readQuery, fields, environment, objectMapper);

        GraphQLServiceUtil.parseSorts(
                readQuery, fields, environment, objectMapper);

        readQuery.setFields(fields);

        Integer offset = null;
        if (environment.containsArgument(GraphQLConstants.QUERY_ARGUMENT_NAME_PAGINATION_OFFSET)) {
            offset = environment.getArgument(GraphQLConstants.QUERY_ARGUMENT_NAME_PAGINATION_OFFSET);
        }

        Integer first = null;
        if (environment.containsArgument(GraphQLConstants.QUERY_ARGUMENT_NAME_PAGINATION_FIRST)) {
            first = environment.getArgument(GraphQLConstants.QUERY_ARGUMENT_NAME_PAGINATION_FIRST);
        }

        PublicQuery query = readQuery;

        if (offset != null && first != null) {
            query = new PageReadQuery(readQuery, offset / first, first);
        }

        return ((ReadResultsData<ReadResult>) queryService.execute(queryContext, query)).getData();
    }

    private Map<String, Object> getChildrenContext(QueryContext queryContext,
                                                   String parentId, String fieldName, String parentTypeName,
                                                   String childTypeName, Map<String, Object> keyContext) {
        Map<String, Object> childrenContext = new LinkedHashMap<>();

        TypeName domainTypeName = GraphQLServiceUtil.toDomainTypeName(childTypeName);

        String parentFieldName = getParentFieldName(fieldName, parentTypeName);

        ReadQuery readQuery = QueryBuilder
                .read(domainTypeName)
                .fields(
                        field(GraphQLConstants.QUERY_ARGUMENT_NAME_ID),
                        field(parentFieldName)
                )
                .conditions(
                        condition(
                                parentFieldName,
                                ConditionClause.EQUALS, ConditionType.String, parentId)
                )
                .getQuery();

        DataFetchingEnvironment dataFetchingEnvironment
                = (DataFetchingEnvironment) keyContext.get(GraphQLConstants.QUERY_DATA_FETCHING_ENVIRONMENT_KEY);

        FieldDefinitions fields = readQuery.getFields();

        GraphQLServiceUtil.parseConditions(
                readQuery, fields, dataFetchingEnvironment, objectMapper);

        GraphQLServiceUtil.parseSorts(
                readQuery, fields, dataFetchingEnvironment, objectMapper);

        readQuery.setFields(fields);

        ReadResults dataSet = queryService.read(queryContext, readQuery);

        for (Map<String, Object> data : dataSet) {
            childrenContext.put(data.get(GraphQLConstants.QUERY_ARGUMENT_NAME_ID).toString(), keyContext);
        }

        return childrenContext;
    }

    private String getParentFieldName(String parentGraphQLFieldName, String parentTypeName) {
        TypeName parentDomainTypeName = GraphQLServiceUtil.toDomainTypeName(parentTypeName);

        DomainType parentDomainType = getSchemaService().getDomainType(parentDomainTypeName);

        ReferenceField referenceField = parentDomainType.getReferenceField(parentGraphQLFieldName);

        if (referenceField != null) {
            return referenceField.getName() + StringUtils.capitalize(GraphQLConstants.QUERY_ARGUMENT_NAME_ID);
        }

        return null;
    }

}
