package org.cheeryworks.liteql.service.jooq;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.cheeryworks.liteql.model.query.QueryContext;
import org.cheeryworks.liteql.service.QueryService;
import org.cheeryworks.liteql.service.query.DefaultAuditingService;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.nio.charset.StandardCharsets;

public class AbstractJooqSqlQueryServiceTest extends AbstractJooqSqlTest {

    private QueryService queryService;

    private QueryContext queryContext = Mockito.mock(QueryContext.class);

    public AbstractJooqSqlQueryServiceTest() {
        super();

        queryService = new JooqSqlQueryService(
                getRepository(), getObjectMapper(), getDslContext(), null,
                new DefaultAuditingService(), Mockito.mock(ApplicationEventPublisher.class));
    }

    protected QueryService getQueryService() {
        return queryService;
    }

    protected QueryContext getQueryContext() {
        return queryContext;
    }

    @Override
    protected String[] getInitSqls() {
        try {
            JooqSqlSchemaParser jooqSqlSchemaParser = new JooqSqlSchemaParser(getRepository(), getDslContext(), null);

            String schemaSqls = jooqSqlSchemaParser.repositoryToSql().replaceAll("\n", "");

            String[] initSqls = schemaSqls.split(";");

            String dataSqls = IOUtils.toString(
                    getClass().getResourceAsStream("/database/init_data_query_service.sql"),
                    StandardCharsets.UTF_8);

            initSqls = ArrayUtils.addAll(initSqls, dataSqls);

            return initSqls;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

}
