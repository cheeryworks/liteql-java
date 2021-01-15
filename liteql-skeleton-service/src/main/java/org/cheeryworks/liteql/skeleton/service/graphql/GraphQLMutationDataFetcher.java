package org.cheeryworks.liteql.skeleton.service.graphql;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import org.cheeryworks.liteql.skeleton.query.AuditQueryContext;
import org.cheeryworks.liteql.skeleton.query.QueryContext;
import org.cheeryworks.liteql.skeleton.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.skeleton.query.save.CreateQuery;
import org.cheeryworks.liteql.skeleton.query.save.UpdateQuery;
import org.cheeryworks.liteql.skeleton.service.query.QueryAccessDecisionService;
import org.cheeryworks.liteql.skeleton.service.query.QueryService;
import org.cheeryworks.liteql.skeleton.service.schema.SchemaService;
import org.cheeryworks.liteql.skeleton.util.GraphQLServiceUtil;
import org.cheeryworks.liteql.skeleton.util.LiteQL;

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

        if (mutationName.startsWith(LiteQL.Constants.GraphQL.MUTATION_NAME_PREFIX_CREATE)) {
            saveQuery = new CreateQuery();
            saveQuery.setDomainTypeName(GraphQLServiceUtil.toDomainTypeName(outputType.getName()));
            saveQuery.setData(environment.getArguments());
        } else if (mutationName.startsWith(LiteQL.Constants.GraphQL.MUTATION_NAME_PREFIX_UPDATE)) {
            saveQuery = new UpdateQuery();
            saveQuery.setDomainTypeName(GraphQLServiceUtil.toDomainTypeName(outputType.getName()));
            saveQuery.setData(environment.getArguments());
        }

        if (saveQuery != null) {
            if (queryContext instanceof AuditQueryContext) {
                getQueryAccessDecisionService().decide(((AuditQueryContext) queryContext).getUser(), saveQuery);
            }

            return getQueryService().save(queryContext, saveQuery).getData();
        } else {
            return super.get(environment);
        }
    }

}
