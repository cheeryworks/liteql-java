package org.cheeryworks.liteql.service.query.condition.converter;

import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.service.query.condition.ConditionValueConverter;

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
