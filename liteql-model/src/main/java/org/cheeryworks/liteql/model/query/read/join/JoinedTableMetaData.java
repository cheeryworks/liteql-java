package org.cheeryworks.liteql.model.query.read.join;

import org.cheeryworks.liteql.model.query.QueryConditions;
import org.cheeryworks.liteql.model.query.read.field.FieldDefinition;

import java.util.LinkedList;

public class JoinedTableMetaData {

    private Class type;

    private Class parentType;

    private String tableName;

    private LinkedList<FieldDefinition> joinedColumns;

    private QueryConditions joinConditions;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public LinkedList<FieldDefinition> getJoinedColumns() {
        return joinedColumns;
    }

    public void setJoinedColumns(LinkedList<FieldDefinition> joinedColumns) {
        this.joinedColumns = joinedColumns;
    }

    public QueryConditions getJoinConditions() {
        return joinConditions;
    }

    public void setJoinConditions(QueryConditions joinConditions) {
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

}
