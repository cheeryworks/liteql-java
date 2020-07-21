package org.cheeryworks.liteql.service.sql;

public abstract class AbstractSqlExecutor extends AbstractSqlService implements SqlExecutor {

    public AbstractSqlExecutor(SqlCustomizer sqlCustomizer) {
        super(sqlCustomizer);
    }

}
