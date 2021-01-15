package org.cheeryworks.liteql.skeleton.util.graphql.builder;

import java.util.LinkedHashMap;
import java.util.Map;

public class GraphQLChildFieldQueryBuilder {

    private Map<String, GraphQLChildFieldQueryBuilder> childFields = new LinkedHashMap<>();

    public static GraphQLChildFieldQueryBuilder newBuilder() {
        return new GraphQLChildFieldQueryBuilder();
    }

    public GraphQLChildFieldQueryBuilder childField(String field) {
        this.childFields.put(field, null);

        return this;
    }

    public GraphQLChildFieldQueryBuilder childField(String field, GraphQLChildFieldQueryBuilder childFieldBuilder) {
        this.childFields.put(field, childFieldBuilder);

        return this;
    }

    public Map<String, GraphQLChildFieldQueryBuilder> getChildFields() {
        return this.childFields;
    }

}
