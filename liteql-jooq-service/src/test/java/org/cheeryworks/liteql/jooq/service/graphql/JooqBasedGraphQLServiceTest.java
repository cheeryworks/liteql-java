package org.cheeryworks.liteql.jooq.service.graphql;

import graphql.ExecutionResult;
import org.cheeryworks.liteql.jooq.service.query.AbstractJooqQueryServiceTest;
import org.cheeryworks.liteql.jooq.service.schema.JooqSchemaParserTest;
import org.cheeryworks.liteql.skeleton.service.graphql.DefaultGraphQLService;
import org.cheeryworks.liteql.skeleton.service.graphql.GraphQLService;
import org.cheeryworks.liteql.skeleton.service.query.DefaultQueryAccessDecisionService;
import org.cheeryworks.liteql.skeleton.util.LiteQL;
import org.cheeryworks.liteql.skeleton.util.graphql.builder.GraphQLBuilder;
import org.cheeryworks.liteql.skeleton.util.graphql.builder.GraphQLChildFieldQueryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cheeryworks.liteql.skeleton.util.graphql.builder.GraphQLEntryFieldQueryBuilder.field;

public class JooqBasedGraphQLServiceTest extends AbstractJooqQueryServiceTest {

    private static Logger logger = LoggerFactory.getLogger(JooqSchemaParserTest.class);

    private GraphQLService graphQLService;

    public JooqBasedGraphQLServiceTest() {
        this.graphQLService = new DefaultGraphQLService(
                getSchemaService(), getQueryService(), new DefaultQueryAccessDecisionService());
    }

    @Test
    public void testingQuery() {
        ExecutionResult result = graphQLService.graphQL(getQueryContext(), GraphQLBuilder
                .query()
                .fields(
                        field("liteql_test__users")
                                .childField("id")
                                .childField("name")
                                .childField("username")
                                .childField("organization", GraphQLChildFieldQueryBuilder.newBuilder()
                                        .childField("id")
                                        .childField("name")
                                )
                                .build()
                )
                .build()
                .getQuery());

        logger.info(LiteQL.JacksonJsonUtils.toJson(result));

        Assertions.assertTrue(result.getErrors().isEmpty());
        Assertions.assertTrue(result.isDataPresent());
    }

}
