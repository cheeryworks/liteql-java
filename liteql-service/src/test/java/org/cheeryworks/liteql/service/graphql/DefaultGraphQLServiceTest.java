package org.cheeryworks.liteql.service.graphql;

import graphql.ExecutionResult;
import org.cheeryworks.liteql.service.jooq.AbstractJooqQueryServiceTest;
import org.cheeryworks.liteql.service.jooq.JooqSchemaParserTest;
import org.cheeryworks.liteql.util.LiteQLUtil;
import org.cheeryworks.liteql.util.graphql.builder.GraphQLBuilder;
import org.cheeryworks.liteql.util.graphql.builder.GraphQLChildFieldQueryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cheeryworks.liteql.util.graphql.builder.GraphQLEntryFieldQueryBuilder.field;

public class DefaultGraphQLServiceTest extends AbstractJooqQueryServiceTest {

    private static Logger logger = LoggerFactory.getLogger(JooqSchemaParserTest.class);

    private GraphQLService graphQLService;

    public DefaultGraphQLServiceTest() {
        this.graphQLService = new DefaultGraphQLService(getLiteQLProperties(), getSchemaService(), getQueryService());
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

        logger.info(LiteQLUtil.toJson(result));

        Assertions.assertTrue(result.getErrors().isEmpty());
        Assertions.assertTrue(result.isDataPresent());
    }

}
