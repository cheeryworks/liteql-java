package org.cheeryworks.liteql.service.jooq;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.cheeryworks.liteql.query.QueryContext;
import org.cheeryworks.liteql.service.query.DefaultQueryAuditingService;
import org.cheeryworks.liteql.service.query.LoggingQueryEventPublisher;
import org.cheeryworks.liteql.service.query.LoggingQueryPublisher;
import org.cheeryworks.liteql.service.query.QueryService;
import org.cheeryworks.liteql.service.query.jooq.JooqQueryExecutor;
import org.cheeryworks.liteql.service.query.jooq.JooqQueryService;
import org.cheeryworks.liteql.service.query.sql.DefaultQueryAccessDecisionService;
import org.cheeryworks.liteql.service.schema.jooq.JooqSchemaParser;
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
                new DefaultQueryAuditingService(), new DefaultQueryAccessDecisionService(),
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
