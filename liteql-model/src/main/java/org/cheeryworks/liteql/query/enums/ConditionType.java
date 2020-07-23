package org.cheeryworks.liteql.query.enums;

import java.math.BigDecimal;
import java.util.Date;

public enum ConditionType {

    Field, String, Long, Integer, Timestamp, Boolean, Decimal;

    public static ConditionType getConditionType(Class javaType) {
        if (String.class.isAssignableFrom(javaType)) {
            return String;
        } else if (long.class.isAssignableFrom(javaType) || Long.class.isAssignableFrom(javaType)) {
            return Long;
        } else if (int.class.isAssignableFrom(javaType) || Integer.class.isAssignableFrom(javaType)) {
            return Integer;
        } else if (boolean.class.isAssignableFrom(javaType) || Boolean.class.isAssignableFrom(javaType)) {
            return Boolean;
        } else if (BigDecimal.class.isAssignableFrom(javaType)) {
            return Decimal;
        } else if (Date.class.isAssignableFrom(javaType)) {
            return Timestamp;
        }

        throw new IllegalArgumentException(javaType.getName() + " not supported");
    }
}
