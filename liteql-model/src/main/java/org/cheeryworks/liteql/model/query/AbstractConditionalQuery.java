package org.cheeryworks.liteql.model.query;

import org.cheeryworks.liteql.model.enums.StandardConditionClause;
import org.cheeryworks.liteql.model.query.condition.ConditionType;
import org.cheeryworks.liteql.model.query.condition.QueryCondition;
import org.cheeryworks.liteql.model.query.condition.QueryConditions;

public abstract class AbstractConditionalQuery extends AbstractQuery {

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

    public void addCondition(String field, StandardConditionClause condition, ConditionType type, Object value) {
        this.addCondition(new QueryCondition(field, condition, type, value));
    }

}
