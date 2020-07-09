package org.cheeryworks.liteql.model.util.graphql.builder;

public class GraphQLEntryChildFieldArgumentQueryBuilder {

    private GraphQLEntryFieldQueryBuilder graphQLEntryFieldQueryBuilder;

    public GraphQLEntryChildFieldArgumentQueryBuilder(GraphQLEntryFieldQueryBuilder graphQLEntryFieldQueryBuilder) {
        this.graphQLEntryFieldQueryBuilder = graphQLEntryFieldQueryBuilder;
    }

    public GraphQLEntryChildFieldArgumentQueryBuilder argument(String argumentName, String variableName) {
        this.graphQLEntryFieldQueryBuilder.getArgumentsMapping().put(argumentName, variableName);

        return this;
    }

    public GraphQLEntryFieldChildFieldQueryBuilder childField(String childField) {
        this.graphQLEntryFieldQueryBuilder.getChildFields().put(childField, null);

        return new GraphQLEntryFieldChildFieldQueryBuilder(this.graphQLEntryFieldQueryBuilder);
    }

    public GraphQLEntryFieldChildFieldQueryBuilder childField(
            String childField, GraphQLChildFieldQueryBuilder graphQLChildFieldQueryBuilder) {
        this.graphQLEntryFieldQueryBuilder.getChildFields().put(childField, graphQLChildFieldQueryBuilder);

        return new GraphQLEntryFieldChildFieldQueryBuilder(this.graphQLEntryFieldQueryBuilder);
    }

}
