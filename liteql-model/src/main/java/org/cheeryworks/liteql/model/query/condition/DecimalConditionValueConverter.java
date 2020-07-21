package org.cheeryworks.liteql.model.query.condition;

import org.cheeryworks.liteql.model.enums.ConditionType;

import java.math.BigDecimal;

public class DecimalConditionValueConverter implements ConditionValueConverter {

    @Override
    public ConditionType getConditionType() {
        return ConditionType.Decimal;
    }

    @Override
    public Object convert(Object value) {
        if (value instanceof BigDecimal) {
            return value;
        } else {
            return new BigDecimal(value.toString());
        }
    }

}
