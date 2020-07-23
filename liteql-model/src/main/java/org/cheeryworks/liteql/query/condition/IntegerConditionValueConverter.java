package org.cheeryworks.liteql.query.condition;

import org.cheeryworks.liteql.query.enums.ConditionType;

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
