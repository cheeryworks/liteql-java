package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.service.AbstractSqlExecutor;
import org.cheeryworks.liteql.service.SqlCustomizer;
import org.cheeryworks.liteql.service.enums.Database;
import org.cheeryworks.liteql.service.jooq.util.JOOQDatabaseTypeUtil;
import org.cheeryworks.liteql.service.util.DatabaseTypeUtil;
import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.jooq.ResultQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class AbstractJooqSqlExecutor extends AbstractSqlExecutor {

    private static Logger logger = LoggerFactory.getLogger(JooqSqlMigrationExecutor.class);

    private DSLContext dslContext;

    private Database database;

    public DSLContext getDslContext() {
        return dslContext;
    }

    public Database getDatabase() {
        return database;
    }

    public AbstractJooqSqlExecutor(DSLContext dslContext, SqlCustomizer sqlCustomizer) {
        super(sqlCustomizer);

        this.dslContext = dslContext;
        this.database = JOOQDatabaseTypeUtil.getDatabase(dslContext.dialect());
    }

    @Override
    public void isDatabaseReady() {
        try {
            getDslContext().fetch(DatabaseTypeUtil.getInstance(getDatabase()).getValidationQuery());

            logger.info("Database is ready");
        } catch (Exception ex) {
            throw new IllegalStateException("Database is not ready");
        }
    }

    @Override
    public long count(String sql, Object[] parameters) {
        ResultQuery resultQuery = getDslContext().resultQuery(sql, parameters);

        long count = (Long) getDslContext().fetchValue(resultQuery);

        return count;
    }

    @Override
    public int execute(String sql, Object[] parameters) {
        return getDslContext().execute(sql, parameters);
    }

    @Override
    public void executeBatch(String sql, List<Object[]> parametersList) {
        BatchBindStep batchBindStep = getDslContext().batch(sql);

        for (Object[] parameters : parametersList) {
            batchBindStep.bind(parameters);
        }

        batchBindStep.execute();
    }

    @Override
    public void executeNamedBatch(String sql, List<Map<String, Object>> parametersList) {
        BatchBindStep batchBindStep = getDslContext().batch(sql);

        for (Map<String, Object> parameters : parametersList) {
            batchBindStep.bind(parameters);
        }

        batchBindStep.execute();
    }

}
