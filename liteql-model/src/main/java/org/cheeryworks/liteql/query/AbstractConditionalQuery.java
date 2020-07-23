package org.cheeryworks.liteql.query;

import org.cheeryworks.liteql.query.enums.ConditionClause;
import org.cheeryworks.liteql.query.enums.ConditionType;

public abstract class AbstractConditionalQuery extends AbstractDomainQuery {

    private QueryConditions conditions;

    public QueryConditions getConditions() {
        return conditions;
    }

    public void setConditions(QueryConditions conditions) {
        this.conditions = conditions;
    }

    public void addCondition(QueryCondition condition) {
        if (this.conditions == null) {
            this.conditions = new QueryConditions();
        }

        this.conditions.add(condition);
    }

    public void addCondition(String field, ConditionClause condition, ConditionType type, Object value) {
        this.addCondition(new QueryCondition(field, condition, type, value));
    }

}
