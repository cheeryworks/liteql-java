package org.cheeryworks.liteql.skeleton.service.query.sql;

import org.cheeryworks.liteql.skeleton.sql.SqlReadQuery;

import java.util.HashMap;
import java.util.Map;

public class InlineSqlReadQuery extends AbstractInlineSqlQuery implements SqlReadQuery {

    private Map<String, String> fields = new HashMap<>();

    private String totalSql;

    private Object[] totalSqlParameters;

    @Override
    public Map<String, String> getFields() {
        return fields;
    }

    @Override
    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    @Override
    public String getTotalSql() {
        return totalSql;
    }

    @Override
    public void setTotalSql(String totalSql) {
        this.totalSql = totalSql;
    }

    @Override
    public Object[] getTotalSqlParameters() {
        return totalSqlParameters;
    }

    @Override
    public void setTotalSqlParameters(Object[] totalSqlParameters) {
        this.totalSqlParameters = totalSqlParameters;
    }

}
