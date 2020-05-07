package org.cheeryworks.liteql.sql.query.condition.converter;

import org.cheeryworks.liteql.model.query.condition.type.DecimalConditionType;
import org.cheeryworks.liteql.sql.query.condition.ConditionValueConverter;

import java.math.BigDecimal;

public class DecimalConditionValueConverter implements ConditionValueConverter<DecimalConditionType> {

    @Override
    public Object convert(DecimalConditionType conditionType, Object value) {
        if (value instanceof BigDecimal) {
            return value;
        } else {
            return new BigDecimal(value.toString());
        }
    }

}
