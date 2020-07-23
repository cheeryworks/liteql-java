package org.cheeryworks.liteql.service.query.jooq;

import org.cheeryworks.liteql.query.QueryCondition;
import org.cheeryworks.liteql.query.read.field.FieldDefinition;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.LinkedList;
import java.util.List;

public class JooqJoinedTable {

    private String tableName;

    private String tableAlias;

    private List<Field<Object>> fields;

    private LinkedList<FieldDefinition> joinedColumns = new LinkedList<>();

    private LinkedList<QueryCondition> joinConditions = new LinkedList<>();

    private Condition condition;

    private Condition joinCondition;

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

    public LinkedList<FieldDefinition> getJoinedColumns() {
        return joinedColumns;
    }

    public void setJoinedColumns(LinkedList<FieldDefinition> joinedColumns) {
        this.joinedColumns = joinedColumns;
    }

    public LinkedList<QueryCondition> getJoinConditions() {
        return joinConditions;
    }

    public void setJoinConditions(LinkedList<QueryCondition> joinConditions) {
        this.joinConditions = joinConditions;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Condition getJoinCondition() {
        return joinCondition;
    }

    public void setJoinCondition(Condition joinCondition) {
        this.joinCondition = joinCondition;
    }

    public List<Field<Object>> getFields() {
        return fields;
    }

    public void setFields(List<Field<Object>> fields) {
        this.fields = fields;
    }

}
