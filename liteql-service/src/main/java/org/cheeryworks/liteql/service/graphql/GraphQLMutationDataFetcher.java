package org.cheeryworks.liteql.service.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import org.cheeryworks.liteql.model.query.QueryContext;
import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.query.save.CreateQuery;
import org.cheeryworks.liteql.model.query.save.UpdateQuery;
import org.cheeryworks.liteql.model.util.LiteQLUtil;
import org.cheeryworks.liteql.model.util.graphql.GraphQLConstants;
import org.cheeryworks.liteql.service.QueryService;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.util.GraphQLServiceUtil;

import java.util.Map;

public class GraphQLMutationDataFetcher extends AbstractGraphQLDataFetcher {

    public GraphQLMutationDataFetcher(
            Repository repository, ObjectMapper objectMapper, QueryService queryService,
            Map<Class, Map<String, String>> graphQLFieldReferences) {
        super(repository, objectMapper, queryService, graphQLFieldReferences);
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        QueryContext queryContext = environment.getContext();

        String mutationName = environment.getFieldDefinition().getDefinition().getName();

        GraphQLObjectType outputType = GraphQLServiceUtil.getWrappedOutputType(environment.getFieldType());

        if (mutationName.startsWith(GraphQLConstants.MUTATION_NAME_PREFIX_CREATE)) {
            AbstractSaveQuery saveQuery = new CreateQuery();
            saveQuery.setDomainTypeName(LiteQLUtil.getTypeName(outputType.getName()));
            saveQuery.setData(environment.getArguments());

            return getQueryService().save(queryContext, saveQuery).getData();
        } else if (mutationName.startsWith(GraphQLConstants.MUTATION_NAME_PREFIX_UPDATE)) {
            AbstractSaveQuery saveQuery = new UpdateQuery();
            saveQuery.setDomainTypeName(LiteQLUtil.getTypeName(outputType.getName()));
            saveQuery.setData(environment.getArguments());

            return getQueryService().save(queryContext, saveQuery).getData();
        } else {
            return super.get(environment);
        }
    }

}
