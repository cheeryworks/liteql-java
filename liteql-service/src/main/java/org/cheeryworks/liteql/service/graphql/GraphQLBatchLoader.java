package org.cheeryworks.liteql.service.graphql;

import org.cheeryworks.liteql.model.enums.ConditionClause;
import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.model.query.QueryContext;
import org.cheeryworks.liteql.model.query.read.ReadQuery;
import org.cheeryworks.liteql.model.query.read.result.ReadResults;
import org.cheeryworks.liteql.model.util.LiteQLUtil;
import org.cheeryworks.liteql.model.util.builder.LiteQLBuilder;
import org.cheeryworks.liteql.model.util.graphql.GraphQLConstants;
import org.cheeryworks.liteql.util.GraphQLServiceUtil;
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

import static org.cheeryworks.liteql.model.util.builder.LiteQLBuilderUtil.condition;

public class GraphQLBatchLoader implements BatchLoaderWithContext<String, Map<String, Object>> {

    private QueryContext queryContext;

    public GraphQLBatchLoader(QueryContext queryContext) {
        this.queryContext = queryContext;
    }

    @Override
    public CompletionStage<List<Map<String, Object>>> load(List<String> keys, BatchLoaderEnvironment environment) {
        return CompletableFuture.completedFuture(query(keys, environment));
    }

    private List<Map<String, Object>> query(List<String> keys, BatchLoaderEnvironment environment) {
        GraphQLBatchLoaderContext context = environment.getContext();

        Map<Object, Object> keyContexts = environment.getKeyContexts();

        Map<String, Map<String, Object>> keysInTypes = new HashMap<>();

        for (String key : keys) {
            Map<String, Object> keyContext = (Map<String, Object>) keyContexts.get(key);

            String type = keyContext.get(GraphQLConstants.QUERY_DOMAIN_TYPE_NAME_KEY).toString();

            if (!keysInTypes.containsKey(type)) {
                keysInTypes.put(type, new HashMap<>());
            }

            if (!keysInTypes.get(type).containsKey(GraphQLConstants.QUERY_DATA_FETCHING_KEYS_KEY)) {
                keysInTypes.get(type).put(GraphQLConstants.QUERY_DATA_FETCHING_KEYS_KEY, new LinkedHashSet<>());
                keysInTypes.get(type).put(
                        GraphQLConstants.QUERY_DATA_FETCHING_ENVIRONMENT_KEY,
                        keyContext.get(GraphQLConstants.QUERY_DATA_FETCHING_ENVIRONMENT_KEY));
            }

            Set<String> keysInType = (Set<String>) keysInTypes
                    .get(type).get(GraphQLConstants.QUERY_DATA_FETCHING_KEYS_KEY);

            keysInType.add(key);
        }

        List<Map<String, Object>> dataSet = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> keysInTypesEntry : keysInTypes.entrySet()) {
            Map<String, Object> keyContext = keysInTypesEntry.getValue();

            String domainTypeName = GraphQLServiceUtil.normalizeGraphQLFieldName(keysInTypesEntry.getKey());

            ReadQuery readQuery = LiteQLBuilder
                    .read(LiteQLUtil.getTypeName(domainTypeName))
                    .fields()
                    .conditions(
                            condition(
                                    GraphQLConstants.QUERY_ARGUMENT_NAME_ID,
                                    ConditionClause.IN, ConditionType.String,
                                    keyContext.get(GraphQLConstants.QUERY_DATA_FETCHING_KEYS_KEY))
                    )
                    .getQuery();

            ReadResults dataSubSet = context.getQueryService().read(queryContext, readQuery);

            dataSet.addAll(dataSubSet);
        }

        Map<String, Map<String, Object>> dataSetInKey = new HashMap<>();

        for (Map<String, Object> data : dataSet) {
            dataSetInKey.put(data.get(GraphQLConstants.QUERY_ARGUMENT_NAME_ID).toString(), data);
        }

        List<Map<String, Object>> sortedDataSet = new ArrayList<>();

        for (String key : keys) {
            sortedDataSet.add(dataSetInKey.get(key));
        }

        return sortedDataSet;
    }


}
