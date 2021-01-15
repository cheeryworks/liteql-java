package org.cheeryworks.liteql.jooq.service.query;

import org.jooq.Condition;
import org.jooq.Field;

import java.util.List;

public class JooqJoinedTable {

    private String tableName;

    private String tableAlias;

    private List<Field<Object>> fields;

    private Condition joinCondition;

    private Condition condition;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    public List<Field<Object>> getFields() {
        return fields;
    }

    public void setFields(List<Field<Object>> fields) {
        this.fields = fields;
    }

    public Condition getJoinCondition() {
        return joinCondition;
    }

    public void setJoinCondition(Condition joinCondition) {
        this.joinCondition = joinCondition;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

}
