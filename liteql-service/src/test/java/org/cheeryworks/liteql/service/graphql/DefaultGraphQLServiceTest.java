package org.cheeryworks.liteql.service.graphql;

import graphql.ExecutionResult;
import org.cheeryworks.liteql.model.util.LiteQLJsonUtil;
import org.cheeryworks.liteql.model.util.graphql.builder.GraphQLBuilder;
import org.cheeryworks.liteql.model.util.graphql.builder.GraphQLChildFieldQueryBuilder;
import org.cheeryworks.liteql.service.GraphQLService;
import org.cheeryworks.liteql.service.jooq.AbstractJooqSqlQueryServiceTest;
import org.cheeryworks.liteql.service.jooq.JooqSqlSchemaParserTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cheeryworks.liteql.model.util.graphql.builder.GraphQLEntryFieldQueryBuilder.field;

public class DefaultGraphQLServiceTest extends AbstractJooqSqlQueryServiceTest {

    private static Logger logger = LoggerFactory.getLogger(JooqSqlSchemaParserTest.class);

    private GraphQLService graphQLService;

    public DefaultGraphQLServiceTest() {
        this.graphQLService = new DefaultGraphQLService(
                getRepository(), getObjectMapper(), getQueryService(), true, false);
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

        logger.info(LiteQLJsonUtil.toJson(getObjectMapper(), result));

        Assertions.assertTrue(result.getErrors().isEmpty());
        Assertions.assertTrue(result.isDataPresent());
    }

}