package org.cheeryworks.liteql.sql.query;

import org.cheeryworks.liteql.model.type.DomainTypeField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InlineSqlReadQuery extends InlineSqlQuery implements SqlReadQuery<List<Object>> {

    private Map<String, DomainTypeField> fields = new HashMap<String, DomainTypeField>();

    private String totalSql;

    private List<Object> totalSqlParameters;

    @Override
    public Map<String, DomainTypeField> getFields() {
        return fields;
    }

    @Override
    public void setFields(Map<String, DomainTypeField> fields) {
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
