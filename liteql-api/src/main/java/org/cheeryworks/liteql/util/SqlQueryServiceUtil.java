package org.cheeryworks.liteql.util;

import org.cheeryworks.liteql.model.enums.DataType;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.field.Field;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public abstract class SqlQueryServiceUtil {

    public static Map<String, Class> getFieldDefinitions(DomainType domainType) {
        Map<String, Class> fieldDefinitions = new HashMap<>();

        for (Field field : domainType.getFields()) {
            fieldDefinitions.put(field.getName(), getDataType(field.getType()));
        }

        return fieldDefinitions;
    }

    private static Class getDataType(DataType dataType) {
        switch (dataType) {
            case Id:
            case Reference:
            case String:
                return String.class;
            case Long:
                return Long.class;
            case Integer:
                return Integer.class;
            case Boolean:
                return Boolean.class;
            case Decimal:
                return BigDecimal.class;
            case Timestamp:
                return Timestamp.class;
            default:
                throw new IllegalArgumentException("Unsupported data type " + dataType);
        }
    }

}
