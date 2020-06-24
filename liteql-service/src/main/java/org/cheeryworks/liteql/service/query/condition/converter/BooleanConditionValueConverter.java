package org.cheeryworks.liteql.service.query.condition.converter;

import org.apache.commons.lang3.BooleanUtils;
import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.service.query.condition.ConditionValueConverter;

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
