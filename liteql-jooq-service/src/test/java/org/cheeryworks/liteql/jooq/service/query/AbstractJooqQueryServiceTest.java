package org.cheeryworks.liteql.jooq.service.query;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.cheeryworks.liteql.jooq.service.AbstractJooqTest;
import org.cheeryworks.liteql.jooq.service.schema.JooqSchemaParser;
import org.cheeryworks.liteql.skeleton.query.QueryContext;
import org.cheeryworks.liteql.skeleton.event.publisher.query.LoggingQueryEventPublisher;
import org.cheeryworks.liteql.skeleton.event.publisher.query.LoggingQueryPublisher;
import org.cheeryworks.liteql.skeleton.service.query.DefaultQueryAuditingService;
import org.cheeryworks.liteql.skeleton.service.query.QueryService;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;

public class AbstractJooqQueryServiceTest extends AbstractJooqTest {

    private QueryService queryService;

    private QueryContext queryContext = Mockito.mock(QueryContext.class);

    public AbstractJooqQueryServiceTest() {
        super();

        queryService = new JooqQueryService(
                getLiteQLProperties(),
                getJooqQueryParser(), new JooqQueryExecutor(getLiteQLProperties(), getDslContext()),
                new DefaultQueryAuditingService(),
                new LoggingQueryPublisher(),
                new LoggingQueryEventPublisher());
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
            JooqSchemaParser jooqSchemaParser = new JooqSchemaParser(
                    getLiteQLProperties(), getSchemaService(), getSqlCustomizer(), getDslContext());

            String schemaSqls = jooqSchemaParser.schemaToSql().replaceAll("\n", "");

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
