package org.cheeryworks.liteql.sql.query.condition.converter;

import org.cheeryworks.liteql.model.query.condition.type.IntegerConditionType;
import org.cheeryworks.liteql.sql.query.condition.ConditionValueConverter;

public class IntegerConditionValueConverter implements ConditionValueConverter<IntegerConditionType> {

    @Override
    public Object convert(IntegerConditionType conditionType, Object value) {
        if (value instanceof Integer) {
            return value;
        } else {
            return Integer.valueOf(value.toString());
        }
    }

}
