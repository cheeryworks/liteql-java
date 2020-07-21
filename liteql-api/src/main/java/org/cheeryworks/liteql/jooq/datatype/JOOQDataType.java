package org.cheeryworks.liteql.jooq.datatype;

import org.cheeryworks.liteql.model.enums.ConditionType;
import org.jooq.DataType;
import org.jooq.impl.SQLDataType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JOOQDataType {

    public static final int STRING_DEFAULT_LENGTH = 255;

    public static final int STRING_MAX_LENGTH = 4000;

    public static final int LONG_LENGTH = 19;

    public static final int INTEGER_MAX_LENGTH = 10;

    public static final int BIG_DECIMAL_PRECISION = 19;

    public static final int BIG_DECIMAL_MIN_SCALE = 2;

    public static final int BIG_DECIMAL_MAX_SCALE = 6;

    public static final Map<Integer, Class> SUPPORTED_DATA_TYPES = new HashMap<>();

    static {
        SUPPORTED_DATA_TYPES.put(Types.VARCHAR, String.class);
        SUPPORTED_DATA_TYPES.put(Types.BIGINT, Long.class);
        SUPPORTED_DATA_TYPES.put(Types.INTEGER, Integer.class);
        SUPPORTED_DATA_TYPES.put(Types.BOOLEAN, Boolean.class);
        SUPPORTED_DATA_TYPES.put(Types.NUMERIC, BigDecimal.class);
        SUPPORTED_DATA_TYPES.put(Types.TIMESTAMP, Timestamp.class);
        SUPPORTED_DATA_TYPES.put(Types.CLOB, String.class);
        SUPPORTED_DATA_TYPES.put(Types.BLOB, byte[].class);
    }

    public static DataType<String> getStringDataType() {
        return SQLDataType.VARCHAR;
    }

    public static DataType<String> getStringDataType(boolean nullable) {
        return getStringDataType().nullable(nullable);
    }

    public static DataType<String> getStringDataType(boolean nullable, int length) {
        return getStringDataType(nullable).length(length);
    }

    public static DataType<Long> getLongDataType() {
        return SQLDataType.BIGINT;
    }

    public static DataType<Long> getLongDataType(boolean nullable) {
        return getLongDataType().nullable(nullable);
    }

    public static DataType<Long> getLongDataType(boolean nullable, int length) {
        return getLongDataType(nullable).length(length);
    }

    public static DataType<Integer> getIntegerDataType() {
        return SQLDataType.INTEGER;
    }

    public static DataType<Integer> getIntegerDataType(boolean nullable) {
        return getIntegerDataType().nullable(nullable);
    }

    public static DataType<Integer> getIntegerDataType(boolean nullable, int length) {
        return getIntegerDataType(nullable).length(length);
    }

    public static DataType<Boolean> getBooleanDataType() {
        return SQLDataType.BOOLEAN.nullable(false);
    }

    public static DataType<BigDecimal> getBigDecimalDataType() {
        return SQLDataType.DECIMAL(BIG_DECIMAL_PRECISION, BIG_DECIMAL_MIN_SCALE);
    }

    public static DataType<BigDecimal> getBigDecimalDataType(boolean nullable) {
        return getBigDecimalDataType().nullable(nullable);
    }

    public static DataType<BigDecimal> getBigDecimalDataType(boolean nullable, int precision, int scale) {
        return getBigDecimalDataType(nullable).precision(precision).scale(scale);
    }

    public static DataType<Timestamp> getTimestampDataType() {
        return SQLDataType.TIMESTAMP;
    }

    public static DataType<Timestamp> getTimestampDataType(boolean nullable) {
        return getTimestampDataType().nullable(nullable);
    }

    public static DataType<String> getClobDataType() {
        return SQLDataType.CLOB;
    }

    public static DataType<String> getClobDataType(boolean nullable) {
        return getClobDataType().nullable(nullable);
    }

    public static DataType<byte[]> getBlobDataType() {
        return SQLDataType.BLOB;
    }

    public static DataType<byte[]> getBlobDataType(boolean nullable) {
        return getBlobDataType().nullable(nullable);
    }

    public static DataType<?> getDataType(Class<?> type) {
        if (String.class.isAssignableFrom(type)) {
            return getStringDataType();
        } else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            return getLongDataType();
        } else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            return getIntegerDataType();
        } else if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
            return getBooleanDataType();
        } else if (BigDecimal.class.isAssignableFrom(type)) {
            return getBigDecimalDataType();
        } else if (Date.class.isAssignableFrom(type)) {
            return getTimestampDataType();
        }

        throw new IllegalArgumentException(type.getName() + " not supported");
    }

    public static DataType<?> getDataType(ConditionType conditionType) {
        if (conditionType == null) {
            conditionType = ConditionType.String;
        }

        if (conditionType.equals(ConditionType.Integer)) {
            return JOOQDataType.getIntegerDataType();
        } else if (conditionType.equals(ConditionType.Boolean)) {
            return JOOQDataType.getBooleanDataType();
        } else if (conditionType.equals(ConditionType.Decimal)) {
            return JOOQDataType.getBigDecimalDataType();
        } else if (conditionType.equals(ConditionType.Timestamp)) {
            return JOOQDataType.getTimestampDataType();
        } else if (conditionType.equals(ConditionType.String)) {
            return JOOQDataType.getStringDataType();
        }

        throw new IllegalArgumentException(
                "Condition type " + conditionType.name() + " not mapping with JOOQ DataType");
    }

}
