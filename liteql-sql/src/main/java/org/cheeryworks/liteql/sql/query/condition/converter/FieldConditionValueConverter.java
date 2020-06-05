package org.cheeryworks.liteql.sql.query.condition.converter;

import org.cheeryworks.liteql.model.enums.ConditionType;
import org.cheeryworks.liteql.sql.query.condition.ConditionValueConverter;

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
