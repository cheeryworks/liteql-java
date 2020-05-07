package org.cheeryworks.liteql.sql.query.condition.converter;

import org.cheeryworks.liteql.model.query.condition.type.BooleanConditionType;
import org.cheeryworks.liteql.sql.query.condition.ConditionValueConverter;
import org.apache.commons.lang3.BooleanUtils;

public class BooleanConditionValueConverter implements ConditionValueConverter<BooleanConditionType> {

    @Override
    public Object convert(BooleanConditionType conditionType, Object value) {
        if (value instanceof Boolean) {
            return value;
        } else {
            return BooleanUtils.toBoolean(value.toString());
        }
    }

}
