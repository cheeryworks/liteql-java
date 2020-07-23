package org.cheeryworks.liteql.service.graphql;

import graphql.ExecutionResult;
import org.cheeryworks.liteql.query.QueryContext;
import org.cheeryworks.liteql.service.json.AbstractServiceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class GraphQLServiceController extends AbstractServiceController {

    private static final String INTROSPECTION_QUERY;

    static {
        StringBuilder introspectionQueryBuilder = new StringBuilder();

        introspectionQueryBuilder.append("  query IntrospectionQuery {\n")
                .append("__schema {\n")
                .append("      queryType { name }\n")
                .append("      mutationType { name }\n")
                .append("      types {\n")
                .append("        ...FullType\n")
                .append("      }\n")
                .append("      directives {\n")
                .append("        name\n")
                .append("        description\n")
                .append("        args {\n")
                .append("          ...InputValue\n")
                .append("        }\n")
                .append("        onOperation\n")
                .append("        onFragment\n")
                .append("        onField\n")
                .append("      }\n")
                .append("    }\n")
                .append("  }\n")
                .append("\n")
                .append("  fragment FullType on __Type {\n")
                .append("    kind\n")
                .append("    name\n")
                .append("    description\n")
                .append("    fields {\n")
                .append("      name\n")
                .append("      description\n")
                .append("      args {\n")
                .append("        ...InputValue\n")
                .append("      }\n")
                .append("      type {\n")
                .append("        ...TypeRef\n")
                .append("      }\n")
                .append("      isDeprecated\n")
                .append("      deprecationReason\n")
                .append("    }\n")
                .append("    inputFields {\n")
                .append("      ...InputValue\n")
                .append("    }\n")
                .append("    interfaces {\n")
                .append("      ...TypeRef\n")
                .append("    }\n")
                .append("    enumValues {\n")
                .append("      name\n")
                .append("      description\n")
                .append("      isDeprecated\n")
                .append("      deprecationReason\n")
                .append("    }\n")
                .append("    possibleTypes {\n")
                .append("      ...TypeRef\n")
                .append("    }\n")
                .append("  }\n")
                .append("\n")
                .append("  fragment InputValue on __InputValue {\n")
                .append("    name\n")
                .append("    description\n")
                .append("    type { ...TypeRef }\n")
                .append("    defaultValue\n")
                .append("  }\n")
                .append("\n")
                .append("  fragment TypeRef on __Type {\n")
                .append("    kind\n")
                .append("    name\n")
                .append("    ofType {\n")
                .append("      kind\n")
                .append("      name\n")
                .append("      ofType {\n")
                .append("        kind\n")
                .append("        name\n")
                .append("        ofType {\n")
                .append("          kind\n")
                .append("          name\n")
                .append("        }\n")
                .append("      }\n")
                .append("    }\n")
                .append("  }\n");

        INTROSPECTION_QUERY = introspectionQueryBuilder.toString();
    }

    @Autowired
    private GraphQLService graphQLService;

    @PostMapping(value = "/graphql")
    public Object graphQL(@RequestBody Map<String, Object> body, QueryContext queryContext) {
        String query = (String) body.get("query");
        if (query == null) {
            query = "";
        }
        String operationName = (String) body.get("operationName");
        Map<String, Object> variables = (Map<String, Object>) body.get("variables");
        if (variables == null) {
            variables = new LinkedHashMap<>();
        }

        ExecutionResult executionResult = graphQLService.graphQL(queryContext, query, operationName, variables);

        Map<String, Object> results = executionResult.toSpecification();

        if (executionResult.getErrors() != null && !executionResult.getErrors().isEmpty()) {
            return getErrorResponseEntity(results);
        }

        return getOkResponseEntity(results);
    }

    @GetMapping(value = "/graphql/schema")
    public Object graphQLSchema(QueryContext queryContext) {
        return this.graphQLService.graphQL(queryContext, INTROSPECTION_QUERY).toSpecification();
    }

}
