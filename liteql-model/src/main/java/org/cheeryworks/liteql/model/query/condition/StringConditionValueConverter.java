package org.cheeryworks.liteql.model.query.condition;

import org.cheeryworks.liteql.model.enums.ConditionType;

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
