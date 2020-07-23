package org.cheeryworks.liteql.query.condition.converter;

import org.cheeryworks.liteql.query.enums.ConditionType;

public class FieldConditionValueConverter implements ConditionValueConverter {

    @Override
    public ConditionType getConditionType() {
        return ConditionType.Field;
    }

    @Override
    public Object convert(Object value) {
        return value;
    }

}
