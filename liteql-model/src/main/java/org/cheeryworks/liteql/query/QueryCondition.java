package org.cheeryworks.liteql.query;

import org.cheeryworks.liteql.query.enums.ConditionClause;
import org.cheeryworks.liteql.query.enums.ConditionOperator;
import org.cheeryworks.liteql.query.enums.ConditionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryCondition implements Serializable {

    private ConditionOperator operator = ConditionOperator.AND;

    private String field;

    private ConditionClause condition = ConditionClause.EQUALS;

    private ConditionType type = ConditionType.String;

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

    public ConditionClause getCondition() {
        return condition;
    }

    public void setCondition(ConditionClause condition) {
        this.condition = condition;
    }

    public ConditionType getType() {
        if (type == null && value != null) {
            if (value instanceof List) {
                return ConditionType.valueOf(((List) value).get(0).getClass().getSimpleName());
            } else if (ConditionClause.LENGTH.equals(condition)) {
                return ConditionType.String;
            } else {
                return ConditionType.valueOf(value.getClass().getSimpleName());
            }
        }

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

    public QueryCondition(
            String field, Object value) {
        this(ConditionOperator.AND, field, ConditionClause.EQUALS, null, value);
    }

    public QueryCondition(
            ConditionOperator operator, String field, ConditionClause condition, ConditionType type, Object value) {
        this.operator = operator;
        this.field = field;
        this.condition = condition;
        this.type = type;
        this.value = value;
    }

    public QueryCondition(String field, ConditionClause condition, ConditionType type, Object value) {
        this.field = field;
        this.condition = condition;
        this.type = type;
        this.value = value;
    }

    public QueryCondition conditions(QueryCondition... queryConditions) {
        for (QueryCondition queryCondition : queryConditions) {
            if (this.getConditions() == null) {
                this.setConditions(new QueryConditions());
            }

            this.getConditions().add(queryCondition);
        }

        return this;
    }

    public static List<Map<String, Object>> asMap(List<QueryCondition> queryConditions) {
        List<Map<String, Object>> queryConditionsInMap = new ArrayList<>();

        for (QueryCondition queryCondition : queryConditions) {
            Map<String, Object> data = new LinkedHashMap<>();

            data.put("operator", queryCondition.getOperator().name());
            data.put("field", queryCondition.getField());
            data.put("condition", queryCondition.getCondition().name());
            data.put("type", queryCondition.getType().name());
            data.put("value", queryCondition.getValue());

            if (queryCondition.getConditions() != null && queryCondition.getConditions().size() > 0) {
                data.put("conditions", asMap(queryCondition.getConditions()));
            }
        }

        return queryConditionsInMap;
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
