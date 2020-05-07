package org.cheeryworks.liteql.sql.query.condition.converter;

import org.cheeryworks.liteql.model.query.condition.type.StringConditionType;
import org.cheeryworks.liteql.sql.query.condition.ConditionValueConverter;

public class StringConditionValueConverter implements ConditionValueConverter<StringConditionType> {

    @Override
    public Object convert(StringConditionType conditionType, Object value) {
        return value;
    }

}
