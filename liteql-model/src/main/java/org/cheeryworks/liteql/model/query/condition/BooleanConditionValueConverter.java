package org.cheeryworks.liteql.model.query.condition;

import org.apache.commons.lang3.BooleanUtils;
import org.cheeryworks.liteql.model.enums.ConditionType;

public class BooleanConditionValueConverter implements ConditionValueConverter {

    @Override
    public ConditionType getConditionType() {
        return ConditionType.Boolean;
    }

    @Override
    public Object convert(Object value) {
        if (value instanceof Boolean) {
            return value;
        } else {
            return BooleanUtils.toBoolean(value.toString());
        }
    }

}
