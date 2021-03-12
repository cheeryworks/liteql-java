package org.cheeryworks.liteql.skeleton.service.graphql;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import org.cheeryworks.liteql.skeleton.query.AuditQueryContext;
import org.cheeryworks.liteql.skeleton.query.QueryContext;
import org.cheeryworks.liteql.skeleton.query.delete.DeleteQuery;
import org.cheeryworks.liteql.skeleton.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.skeleton.query.save.CreateQuery;
import org.cheeryworks.liteql.skeleton.query.save.UpdateQuery;
import org.cheeryworks.liteql.skeleton.service.query.QueryAccessDecisionService;
import org.cheeryworks.liteql.skeleton.service.query.QueryService;
import org.cheeryworks.liteql.skeleton.service.schema.SchemaService;
import org.cheeryworks.liteql.skeleton.util.GraphQLServiceUtil;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.cheeryworks.liteql.skeleton.util.LiteQL.Constants.GraphQL.DELETE_RESULT_FIELD_COUNT_NAME;
import static org.cheeryworks.liteql.skeleton.util.LiteQL.Constants.GraphQL.INPUT_RAW_ARGUMENT_NAME;
import static org.cheeryworks.liteql.skeleton.util.LiteQL.Constants.GraphQL.MUTATION_NAME_PREFIX_CREATE;
import static org.cheeryworks.liteql.skeleton.util.LiteQL.Constants.GraphQL.MUTATION_NAME_PREFIX_DELETE;
import static org.cheeryworks.liteql.skeleton.util.LiteQL.Constants.GraphQL.MUTATION_NAME_PREFIX_UPDATE;

public class GraphQLMutationDataFetcher extends AbstractGraphQLDataFetcher {

    public GraphQLMutationDataFetcher(
            SchemaService schemaService, QueryService queryService,
            QueryAccessDecisionService queryAccessDecisionService) {
        super(schemaService, queryService, queryAccessDecisionService);
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        QueryContext queryContext = environment.getContext();

        String mutationName = environment.getFieldDefinition().getDefinition().getName();

        GraphQLObjectType outputType = GraphQLServiceUtil.getWrappedOutputType(environment.getFieldType());

        AbstractSaveQuery saveQuery = null;

        DeleteQuery deleteQuery = null;

        if (mutationName.startsWith(MUTATION_NAME_PREFIX_CREATE)) {
            saveQuery = new CreateQuery();
            saveQuery.setDomainTypeName(GraphQLServiceUtil.graphQLTypeNameToDomainTypeName(outputType.getName()));
            saveQuery.setData((Map<String, Object>) environment.getArguments().get(INPUT_RAW_ARGUMENT_NAME));
        } else if (mutationName.startsWith(MUTATION_NAME_PREFIX_UPDATE)) {
            saveQuery = new UpdateQuery();
            saveQuery.setDomainTypeName(GraphQLServiceUtil.graphQLTypeNameToDomainTypeName(outputType.getName()));
            saveQuery.setData(environment.getArguments());
        } else if (mutationName.startsWith(MUTATION_NAME_PREFIX_DELETE)) {
            deleteQuery = new DeleteQuery();
            deleteQuery.setDomainTypeName(GraphQLServiceUtil.graphQLTypeNameToDomainTypeName(outputType.getName()));

            GraphQLServiceUtil.parseConditions(deleteQuery, environment);
        }

        if (saveQuery != null) {
            if (queryContext instanceof AuditQueryContext) {
                getQueryAccessDecisionService().decide(((AuditQueryContext) queryContext).getUser(), saveQuery);
            }

            return getQueryService().save(queryContext, saveQuery).getData();
        } else if (deleteQuery != null) {
            if (queryContext instanceof AuditQueryContext) {
                getQueryAccessDecisionService().decide(((AuditQueryContext) queryContext).getUser(), deleteQuery);
            }

            int count = getQueryService().delete(queryContext, deleteQuery);

            Map<String, Object> result = new LinkedHashMap<>();

            result.put(DELETE_RESULT_FIELD_COUNT_NAME, count);

            return result;
        } else {
            return super.get(environment);
        }
    }

}
