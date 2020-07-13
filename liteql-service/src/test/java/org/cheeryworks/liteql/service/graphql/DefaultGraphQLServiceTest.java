package org.cheeryworks.liteql.service.graphql;

import org.cheeryworks.liteql.service.GraphQLService;
import org.cheeryworks.liteql.service.jooq.AbstractJooqSqlQueryServiceTest;

public class DefaultGraphQLServiceTest extends AbstractJooqSqlQueryServiceTest {

    private GraphQLService graphQLService;

    public DefaultGraphQLServiceTest() {
        this.graphQLService = new DefaultGraphQLService(getRepository(), getObjectMapper(), getQueryService(), true);
    }


}
