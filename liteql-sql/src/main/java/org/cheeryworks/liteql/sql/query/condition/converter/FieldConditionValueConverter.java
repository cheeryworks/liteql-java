package org.cheeryworks.liteql.sql.query.condition.converter;

import org.cheeryworks.liteql.model.query.condition.type.FieldConditionType;
import org.cheeryworks.liteql.sql.query.condition.ConditionValueConverter;

public class FieldConditionValueConverter implements ConditionValueConverter<FieldConditionType> {

    @Override
    public Object convert(FieldConditionType conditionType, Object value) {
        return value;
    }

}
