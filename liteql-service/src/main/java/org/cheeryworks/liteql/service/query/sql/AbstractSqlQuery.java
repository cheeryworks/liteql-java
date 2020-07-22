package org.cheeryworks.liteql.service.query.sql;

import org.cheeryworks.liteql.sql.SqlQuery;

public abstract class AbstractSqlQuery<T> implements SqlQuery<T> {

    private String sql;

    private T sqlParameters;

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public T getSqlParameters() {
        return sqlParameters;
    }

    @Override
    public void setSqlParameters(T sqlParameters) {
        this.sqlParameters = sqlParameters;
    }

}
