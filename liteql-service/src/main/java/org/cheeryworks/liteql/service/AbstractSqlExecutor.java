package org.cheeryworks.liteql.service;

public abstract class AbstractSqlExecutor extends AbstractSqlService implements SqlExecutor {

    public AbstractSqlExecutor(SqlCustomizer sqlCustomizer) {
        super(sqlCustomizer);
    }

}
