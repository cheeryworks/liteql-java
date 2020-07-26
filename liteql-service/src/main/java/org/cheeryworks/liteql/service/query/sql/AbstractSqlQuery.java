package org.cheeryworks.liteql.service.query.sql;

import org.cheeryworks.liteql.sql.SqlQuery;

public abstract class AbstractSqlQuery implements SqlQuery {

    private String sql;

    private Object[] sqlParameters;

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public Object[] getSqlParameters() {
        return sqlParameters;
    }

    @Override
    public void setSqlParameters(Object[] sqlParameters) {
        this.sqlParameters = sqlParameters;
    }

}
