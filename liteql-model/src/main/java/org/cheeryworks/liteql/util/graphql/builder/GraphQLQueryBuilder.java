package org.cheeryworks.liteql.util.graphql.builder;

import org.cheeryworks.liteql.graphql.GraphQLQuery;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class GraphQLQueryBuilder {

    private static final String OPERATION_NAME = "getData";

    private Map<String, Object> variables = new LinkedHashMap<>();

    private Map<String, String> variableTypes = new LinkedHashMap<>();

    private GraphQLEntryFieldQueryBuilder[] graphQLEntryFieldQueryBuilders;

    public void addVariable(String key, Object value, String type) {
        this.variables.put(key, value);

        this.variableTypes.put(key, type);
    }

    public GraphQLEntryFieldQueryBuilder[] getGraphQLEntryFieldQueryBuilders() {
        return graphQLEntryFieldQueryBuilders;
    }

    public void setGraphQLEntryFieldQueryBuilders(GraphQLEntryFieldQueryBuilder[] graphQLEntryFieldQueryBuilders) {
        this.graphQLEntryFieldQueryBuilders = graphQLEntryFieldQueryBuilders;
    }

    public static GraphQLVariableQueryBuilder query() {
        GraphQLQueryBuilder graphQLQueryBuilder = new GraphQLQueryBuilder();

        return new GraphQLVariableQueryBuilder(graphQLQueryBuilder);
    }

    public GraphQLQuery build() {
        GraphQLQuery graphQLQuery = new GraphQLQuery();

        graphQLQuery.setOperationName(OPERATION_NAME);
        graphQLQuery.setVariables(this.variables);

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("query ").append(OPERATION_NAME);

        if (!this.variables.isEmpty()) {
            queryBuilder.append("(");

            Iterator<String> variableIterator = variables.keySet().iterator();

            while (variableIterator.hasNext()) {
                String variableName = variableIterator.next();

                queryBuilder.append("$").append(variableName).append(": ").append(variableTypes.get(variableName));

                if (variableIterator.hasNext()) {
                    queryBuilder.append(", ");
                }
            }

            queryBuilder.append(")");
        }

        queryBuilder.append(" {\n");

        for (GraphQLEntryFieldQueryBuilder graphQLEntryFieldQueryBuilder : getGraphQLEntryFieldQueryBuilders()) {
            graphQLEntryFieldQueryBuilder.buildQuery(queryBuilder);
        }

        queryBuilder.append("}");

        graphQLQuery.setQuery(queryBuilder.toString());

        return graphQLQuery;
    }

}
