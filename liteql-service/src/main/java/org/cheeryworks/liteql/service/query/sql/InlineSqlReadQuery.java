package org.cheeryworks.liteql.service.query.sql;

import org.cheeryworks.liteql.model.type.field.Field;
import org.cheeryworks.liteql.sql.SqlReadQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InlineSqlReadQuery extends InlineSqlQuery implements SqlReadQuery<List<Object>> {

    private Map<String, Field> fields = new HashMap<>();

    private String totalSql;

    private List<Object> totalSqlParameters;

    @Override
    public Map<String, Field> getFields() {
        return fields;
    }

    @Override
    public void setFields(Map<String, Field> fields) {
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
    public List<Object> getTotalSqlParameters() {
        return totalSqlParameters;
    }

    @Override
    public void setTotalSqlParameters(List<Object> totalSqlParameters) {
        this.totalSqlParameters = totalSqlParameters;
    }

}
