package org.cheeryworks.liteql.util.graphql.builder;

public class GraphQLVariableQueryBuilder {

    private GraphQLQueryBuilder graphQLQueryBuilder;

    public GraphQLVariableQueryBuilder(GraphQLQueryBuilder graphQLQueryBuilder) {
        this.graphQLQueryBuilder = graphQLQueryBuilder;
    }

    public GraphQLVariableQueryBuilder variable(String key, Object value, String type) {
        this.graphQLQueryBuilder.addVariable(key, value, type);

        return this;
    }

    public GraphQLQueryBuilder fields(GraphQLEntryFieldQueryBuilder... graphQLEntryFieldQueryBuilders) {
        this.graphQLQueryBuilder.setGraphQLEntryFieldQueryBuilders(graphQLEntryFieldQueryBuilders);

        return this.graphQLQueryBuilder;
    }

}
