package org.cheeryworks.liteql.spring.service;

import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.enums.Database;
import org.cheeryworks.liteql.skeleton.service.query.sql.AbstractSqlExecutor;
import org.cheeryworks.liteql.skeleton.util.DatabaseUtil;
import org.cheeryworks.liteql.spring.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public abstract class AbstractJdbcExecutor extends AbstractSqlExecutor {

    private static Logger logger = LoggerFactory.getLogger(AbstractJdbcExecutor.class);

    private JdbcTemplate jdbcTemplate;

    private Database database;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public Database getDatabase() {
        return database;
    }

    public AbstractJdbcExecutor(LiteQLProperties liteQLProperties, JdbcTemplate jdbcTemplate) {
        super(liteQLProperties);

        this.jdbcTemplate = jdbcTemplate;
        this.database = SpringUtils.getDatabase(jdbcTemplate.getDataSource());
    }

    @Override
    public void isDatabaseReady() {
        try {
            getJdbcTemplate().execute(DatabaseUtil.getInstance(getDatabase()).getValidationQuery());

            logger.info("Database is ready");
        } catch (Exception ex) {
            throw new IllegalStateException("Database is not ready");
        }
    }

    @Override
    public long count(String sql, Object[] parameters) {
        return getJdbcTemplate().queryForObject(sql, Long.class, parameters);
    }

    @Override
    public int execute(String sql, Object[] parameters) {
        return getJdbcTemplate().update(sql, parameters);
    }

    @Override
    public void executeBatch(String sql, List<Object[]> parametersList) {
        getJdbcTemplate().batchUpdate(sql, parametersList);
    }

}
