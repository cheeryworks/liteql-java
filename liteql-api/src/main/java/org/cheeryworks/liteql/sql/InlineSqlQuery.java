package org.cheeryworks.liteql.sql;

import java.util.Arrays;
import java.util.List;

public abstract class InlineSqlQuery extends AbstractSqlQuery<List<Object>> {

    private String sql;

    private List<Object> sqlParameters;

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public List<Object> getSqlParameters() {
        return sqlParameters;
    }

    @Override
    public void setSqlParameters(List<Object> sqlParameters) {
        this.sqlParameters = sqlParameters;
    }

    @Override
    public String toString() {
        return "SQL: " + getSql() + "\n"
                + "SQL Parameters: " + Arrays.toString(getSqlParameters().toArray());
    }

}
