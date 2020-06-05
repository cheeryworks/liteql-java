package org.cheeryworks.liteql.sql.query.condition.converter;

import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.sql.query.condition.ConditionValueConverter;

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
