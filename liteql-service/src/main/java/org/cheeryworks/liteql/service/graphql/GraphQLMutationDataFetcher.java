package org.cheeryworks.liteql.service.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import org.cheeryworks.liteql.model.query.QueryContext;
import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.query.save.CreateQuery;
import org.cheeryworks.liteql.model.query.save.UpdateQuery;
import org.cheeryworks.liteql.model.util.graphql.GraphQLConstants;
import org.cheeryworks.liteql.service.query.QueryService;
import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.util.GraphQLServiceUtil;

public class GraphQLMutationDataFetcher extends AbstractGraphQLDataFetcher {

    public GraphQLMutationDataFetcher(
            Repository repository, ObjectMapper objectMapper, QueryService queryService) {
        super(repository, objectMapper, queryService);
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        QueryContext queryContext = environment.getContext();

        String mutationName = environment.getFieldDefinition().getDefinition().getName();

        GraphQLObjectType outputType = GraphQLServiceUtil.getWrappedOutputType(environment.getFieldType());

        if (mutationName.startsWith(GraphQLConstants.MUTATION_NAME_PREFIX_CREATE)) {
            AbstractSaveQuery saveQuery = new CreateQuery();
            saveQuery.setDomainTypeName(GraphQLServiceUtil.toDomainTypeName(outputType.getName()));
            saveQuery.setData(environment.getArguments());

            return getQueryService().save(queryContext, saveQuery).getData();
        } else if (mutationName.startsWith(GraphQLConstants.MUTATION_NAME_PREFIX_UPDATE)) {
            AbstractSaveQuery saveQuery = new UpdateQuery();
            saveQuery.setDomainTypeName(GraphQLServiceUtil.toDomainTypeName(outputType.getName()));
            saveQuery.setData(environment.getArguments());

            return getQueryService().save(queryContext, saveQuery).getData();
        } else {
            return super.get(environment);
        }
    }

}
