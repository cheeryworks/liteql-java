package org.cheeryworks.liteql.model.query.condition;

import org.cheeryworks.liteql.model.enums.ConditionType;

public class IntegerConditionValueConverter implements ConditionValueConverter {

    @Override
    public ConditionType getConditionType() {
        return ConditionType.Integer;
    }

    @Override
    public Object convert(Object value) {
        if (value instanceof Integer) {
            return value;
        } else {
            return Integer.valueOf(value.toString());
        }
    }

}
