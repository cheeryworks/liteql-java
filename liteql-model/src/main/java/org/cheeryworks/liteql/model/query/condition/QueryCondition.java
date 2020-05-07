package org.cheeryworks.liteql.model.query.condition;

import org.cheeryworks.liteql.model.enums.ConditionOperator;
import org.cheeryworks.liteql.model.enums.StandardConditionClause;

import java.io.Serializable;

public class QueryCondition implements Serializable {

    private ConditionOperator operator = ConditionOperator.AND;

    private String field;

    private StandardConditionClause condition = StandardConditionClause.EQUALS;

    private ConditionType type;

    private Object value;

    private QueryConditions conditions;

    public ConditionOperator getOperator() {
        return operator;
    }

    public void setOperator(ConditionOperator operator) {
        this.operator = operator;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public StandardConditionClause getCondition() {
        return condition;
    }

    public void setCondition(StandardConditionClause condition) {
        this.condition = condition;
    }

    public ConditionType getType() {
        return type;
    }

    public void setType(ConditionType type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public QueryConditions getConditions() {
        return conditions;
    }

    public void setConditions(QueryConditions conditions) {
        this.conditions = conditions;
    }

    public QueryCondition() {

    }

    public QueryCondition(String field, StandardConditionClause condition, ConditionType type, Object value) {
        this.field = field;
        this.condition = condition;
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "QueryCondition{"
                + "operator=" + operator
                + ", field='" + field + '\''
                + ", condition=" + condition
                + ", type=" + type
                + ", value=" + value
                + '}';
    }
}
