package org.cheeryworks.liteql.service.query.condition.converter;

import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.service.query.condition.ConditionValueConverter;

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
