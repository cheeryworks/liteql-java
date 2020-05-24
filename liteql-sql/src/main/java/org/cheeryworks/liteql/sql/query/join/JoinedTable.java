package org.cheeryworks.liteql.sql.query.join;

import org.cheeryworks.liteql.model.query.condition.QueryCondition;
import org.cheeryworks.liteql.model.query.field.QueryFieldDefinition;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.LinkedList;
import java.util.List;

public class JoinedTable {

    private Class type;

    private Class parentType;

    private String tableName;

    private String tableAlias;

    private List<Field<Object>> fields;

    private LinkedList<QueryFieldDefinition> joinedColumns = new LinkedList<QueryFieldDefinition>();

    private LinkedList<QueryCondition>
            joinConditions = new LinkedList<QueryCondition>();

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

    public LinkedList<QueryFieldDefinition> getJoinedColumns() {
        return joinedColumns;
    }

    public void setJoinedColumns(LinkedList<QueryFieldDefinition> joinedColumns) {
        this.joinedColumns = joinedColumns;
    }

    public LinkedList<QueryCondition> getJoinConditions() {
        return joinConditions;
    }

    public void setJoinConditions(LinkedList<QueryCondition> joinConditions) {
        this.joinConditions = joinConditions;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public Class getParentType() {
        return parentType;
    }

    public void setParentType(Class parentType) {
        this.parentType = parentType;
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