package org.cheeryworks.liteql.model.query.condition;

import org.cheeryworks.liteql.model.enums.ConditionType;

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
