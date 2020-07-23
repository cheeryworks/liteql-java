package org.cheeryworks.liteql.query.condition.converter;

import org.cheeryworks.liteql.query.enums.ConditionType;

public class LongConditionValueConverter implements ConditionValueConverter {

    @Override
    public ConditionType getConditionType() {
        return ConditionType.Long;
    }

    @Override
    public Object convert(Object value) {
        if (value instanceof Long) {
            return value;
        } else {
            return Long.valueOf(value.toString());
        }
    }

}
