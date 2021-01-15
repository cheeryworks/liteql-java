package org.cheeryworks.liteql.skeleton.util.graphql.builder;

public class GraphQLEntryFieldChildFieldQueryBuilder {

    private GraphQLEntryFieldQueryBuilder graphQLEntryFieldQueryBuilder;

    public GraphQLEntryFieldChildFieldQueryBuilder(
            GraphQLEntryFieldQueryBuilder graphQLEntryFieldQueryBuilder) {
        this.graphQLEntryFieldQueryBuilder = graphQLEntryFieldQueryBuilder;
    }

    public GraphQLEntryFieldChildFieldQueryBuilder childField(String childField) {
        this.graphQLEntryFieldQueryBuilder.getChildFields().put(childField, null);

        return this;
    }

    public GraphQLEntryFieldChildFieldQueryBuilder childField(
            String childField, GraphQLChildFieldQueryBuilder graphQLChildFieldQueryBuilder) {
        this.graphQLEntryFieldQueryBuilder.getChildFields().put(childField, graphQLChildFieldQueryBuilder);

        return this;
    }

    public GraphQLEntryFieldQueryBuilder build() {
        return this.graphQLEntryFieldQueryBuilder;
    }

}
