package org.cheeryworks.liteql.jooq.service;

import org.cheeryworks.liteql.jooq.util.JooqUtil;
import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.enums.Database;
import org.cheeryworks.liteql.skeleton.service.query.sql.AbstractSqlExecutor;
import org.cheeryworks.liteql.skeleton.util.DatabaseUtil;
import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.jooq.ResultQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractJooqExecutor extends AbstractSqlExecutor {

    private static Logger logger = LoggerFactory.getLogger(AbstractJooqExecutor.class);

    private DSLContext dslContext;

    private Database database;

    public DSLContext getDslContext() {
        return dslContext;
    }

    public Database getDatabase() {
        return database;
    }

    public AbstractJooqExecutor(LiteQLProperties liteQLProperties, DSLContext dslContext) {
        super(liteQLProperties);

        this.dslContext = dslContext;
        this.database = JooqUtil.getDatabase(dslContext.dialect());
    }

    @Override
    public void isDatabaseReady() {
        try {
            getDslContext().fetch(DatabaseUtil.getInstance(getDatabase()).getValidationQuery());

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

}
