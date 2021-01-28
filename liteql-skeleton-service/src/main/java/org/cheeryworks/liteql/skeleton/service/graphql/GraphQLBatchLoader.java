package org.cheeryworks.liteql.skeleton.service.graphql;

import graphql.schema.DataFetchingEnvironment;
import org.cheeryworks.liteql.skeleton.query.enums.ConditionClause;
import org.cheeryworks.liteql.skeleton.query.enums.ConditionType;
import org.cheeryworks.liteql.skeleton.query.read.ReadQuery;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResults;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.service.query.QueryService;
import org.cheeryworks.liteql.skeleton.util.GraphQLServiceUtil;
import org.cheeryworks.liteql.skeleton.util.query.builder.QueryBuilder;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.BatchLoaderWithContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.cheeryworks.liteql.skeleton.util.LiteQL.Constants.GraphQL.QUERY_ARGUMENT_NAME_ID;
import static org.cheeryworks.liteql.skeleton.util.LiteQL.Constants.GraphQL.QUERY_DATA_FETCHING_ENVIRONMENT_KEY;
import static org.cheeryworks.liteql.skeleton.util.LiteQL.Constants.GraphQL.QUERY_DATA_FETCHING_KEYS_KEY;
import static org.cheeryworks.liteql.skeleton.util.LiteQL.Constants.GraphQL.QUERY_DOMAIN_TYPE_NAME_KEY;
import static org.cheeryworks.liteql.skeleton.util.query.builder.QueryBuilderUtil.condition;

public class GraphQLBatchLoader implements BatchLoaderWithContext<String, Map<String, Object>> {

    private QueryService queryService;

    public GraphQLBatchLoader(QueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    public CompletionStage<List<Map<String, Object>>> load(List<String> keys, BatchLoaderEnvironment environment) {
        return CompletableFuture.completedFuture(query(keys, environment));
    }

    private List<Map<String, Object>> query(List<String> keys, BatchLoaderEnvironment environment) {
        Map<Object, Object> keyContexts = environment.getKeyContexts();

        Map<String, Map<String, Object>> keysInTypes = new HashMap<>();

        for (String key : keys) {
            Map<String, Object> keyContext = (Map<String, Object>) keyContexts.get(key);

            String type = keyContext.get(QUERY_DOMAIN_TYPE_NAME_KEY).toString();

            if (!keysInTypes.containsKey(type)) {
                keysInTypes.put(type, new HashMap<>());
            }

            if (!keysInTypes.get(type).containsKey(QUERY_DATA_FETCHING_KEYS_KEY)) {
                keysInTypes.get(type).put(
                        QUERY_DATA_FETCHING_KEYS_KEY, new LinkedHashSet<>());
                keysInTypes.get(type).put(
                        QUERY_DATA_FETCHING_ENVIRONMENT_KEY,
                        keyContext.get(QUERY_DATA_FETCHING_ENVIRONMENT_KEY));
            }

            Set<String> keysInType = (Set<String>) keysInTypes
                    .get(type).get(QUERY_DATA_FETCHING_KEYS_KEY);

            keysInType.add(key);
        }

        List<Map<String, Object>> dataSet = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> keysInTypesEntry : keysInTypes.entrySet()) {
            Map<String, Object> keyContext = keysInTypesEntry.getValue();

            TypeName domainTypeName = GraphQLServiceUtil.toDomainTypeName(keysInTypesEntry.getKey());

            ReadQuery readQuery = QueryBuilder
                    .read(domainTypeName)
                    .fields()
                    .conditions(
                            condition(
                                    QUERY_ARGUMENT_NAME_ID,
                                    ConditionClause.IN, ConditionType.String,
                                    keyContext.get(QUERY_DATA_FETCHING_KEYS_KEY))
                    )
                    .getQuery();

            DataFetchingEnvironment dataFetchingEnvironment =
                    (DataFetchingEnvironment) keyContext.get(
                            QUERY_DATA_FETCHING_ENVIRONMENT_KEY);

            GraphQLServiceUtil.parseFields(readQuery, dataFetchingEnvironment);

            ReadResults dataSubSet = queryService.read(dataFetchingEnvironment.getContext(), readQuery);

            dataSet.addAll(dataSubSet);
        }

        Map<String, Map<String, Object>> dataSetInKey = new HashMap<>();

        for (Map<String, Object> data : dataSet) {
            dataSetInKey.put(data.get(QUERY_ARGUMENT_NAME_ID).toString(), data);
        }

        List<Map<String, Object>> sortedDataSet = new ArrayList<>();

        for (String key : keys) {
            sortedDataSet.add(dataSetInKey.get(key));
        }

        return sortedDataSet;
    }


}
