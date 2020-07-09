package org.cheeryworks.liteql.model.util.graphql.builder;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class GraphQLEntryFieldQueryBuilder {

    private String field;

    private Map<String, String> argumentsMapping = new LinkedHashMap<>();

    private Map<String, GraphQLChildFieldQueryBuilder> childFields = new LinkedHashMap<>();

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Map<String, String> getArgumentsMapping() {
        return argumentsMapping;
    }

    public void setArgumentsMapping(Map<String, String> argumentsMapping) {
        this.argumentsMapping = argumentsMapping;
    }

    public Map<String, GraphQLChildFieldQueryBuilder> getChildFields() {
        return childFields;
    }

    public void setChildFields(Map<String, GraphQLChildFieldQueryBuilder> childFields) {
        this.childFields = childFields;
    }

    protected void buildQuery(StringBuilder queryBuilder) {
        queryBuilder.append("  ").append(this.field);

        if (!argumentsMapping.isEmpty()) {
            queryBuilder.append("(");

            Iterator<Map.Entry<String, String>> argumentsMappingIterator = argumentsMapping.entrySet().iterator();

            while (argumentsMappingIterator.hasNext()) {
                Map.Entry<String, String> argumentMapping = argumentsMappingIterator.next();

                queryBuilder.append(argumentMapping.getKey())
                        .append(": ").append("$").append(argumentMapping.getValue());

                if (argumentsMappingIterator.hasNext()) {
                    queryBuilder.append(", ");
                }
            }

            queryBuilder.append(")");
        }

        queryBuilder.append(" ");

        buildQuery(queryBuilder, childFields, 2);
    }

    private void buildQuery(
            StringBuilder queryBuilder, Map<String, GraphQLChildFieldQueryBuilder> childFields, int level) {
        queryBuilder.append("{\n");

        for (Map.Entry<String, GraphQLChildFieldQueryBuilder> childField : childFields.entrySet()) {
            if (childField.getValue() == null) {
                queryBuilder.append(getSpaces(level)).append(childField.getKey()).append("\n");
            } else {
                queryBuilder.append(getSpaces(level)).append(childField.getKey()).append(" ");

                buildQuery(queryBuilder, childField.getValue().getChildFields(), level + 1);
            }
        }

        queryBuilder.append(getSpaces(level - 1)).append("}\n");
    }

    public static String getSpaces(int level) {
        return StringUtils.repeat(" ", level * 2);
    }

    public static GraphQLEntryChildFieldArgumentQueryBuilder field(String field) {
        GraphQLEntryFieldQueryBuilder graphQLEntryFieldQueryBuilder = new GraphQLEntryFieldQueryBuilder();

        graphQLEntryFieldQueryBuilder.setField(field);

        return new GraphQLEntryChildFieldArgumentQueryBuilder(graphQLEntryFieldQueryBuilder);
    }

}
