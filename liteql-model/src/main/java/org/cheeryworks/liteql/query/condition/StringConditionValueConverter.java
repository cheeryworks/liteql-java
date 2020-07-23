package org.cheeryworks.liteql.query.condition;

import org.cheeryworks.liteql.query.enums.ConditionType;

public class StringConditionValueConverter implements ConditionValueConverter {

    @Override
    public ConditionType getConditionType() {
        return ConditionType.String;
    }

    @Override
    public Object convert(Object value) {
        return value;
    }

}
