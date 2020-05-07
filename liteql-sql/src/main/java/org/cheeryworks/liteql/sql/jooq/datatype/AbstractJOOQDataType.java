package org.cheeryworks.liteql.sql.jooq.datatype;

import org.jooq.DataType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public abstract class AbstractJOOQDataType implements JOOQDataType {

    protected abstract DataType<String> getJOOQStringDataType();

    @Override
    public DataType<String> getStringDataType() {
        return getJOOQStringDataType().length(STRING_DEFAULT_LENGTH);
    }

    @Override
    public DataType<String> getStringDataType(boolean nullable) {
        return getStringDataType().nullable(nullable);
    }

    @Override
    public DataType<String> getStringDataType(int length, boolean nullable) {
        return getStringDataType(nullable).length(length);
    }

    protected abstract DataType<Long> getJOOQLongDataType();

    @Override
    public DataType<Long> getLongDataType() {
        return getJOOQLongDataType().length(LONG_LENGTH);
    }

    @Override
    public DataType<Long> getLongDataType(boolean nullable) {
        return getLongDataType().nullable(nullable);
    }

    @Override
    public DataType<Long> getLongDataType(int length, boolean nullable) {
        return getLongDataType(nullable).length(length);
    }

    protected abstract DataType<Integer> getJOOQIntegerDataType();

    @Override
    public DataType<Integer> getIntegerDataType() {
        return getJOOQIntegerDataType().length(INTEGER_MAX_LENGTH);
    }

    @Override
    public DataType<Integer> getIntegerDataType(boolean nullable) {
        return getIntegerDataType().nullable(nullable);
    }

    @Override
    public DataType<Integer> getIntegerDataType(int length, boolean nullable) {
        return getIntegerDataType(nullable).length(length);
    }

    protected abstract DataType<Boolean> getJOOQBooleanDataType();

    @Override
    public DataType<Boolean> getBooleanDataType() {
        return getBooleanDataType(true);
    }

    @Override
    public DataType<Boolean> getBooleanDataType(boolean nullable) {
        return getJOOQBooleanDataType().nullable(nullable);
    }

    protected abstract DataType<BigDecimal> getJOOQBigDecimalDataType();

    @Override
    public DataType<BigDecimal> getBigDecimalDataType() {
        return getJOOQBigDecimalDataType().precision(BIG_DECIMAL_PRECISION).scale(BIG_DECIMAL_MIN_SCALE);
    }

    @Override
    public DataType<BigDecimal> getBigDecimalDataType(boolean nullable) {
        return getBigDecimalDataType().nullable(nullable);
    }

    @Override
    public DataType<BigDecimal> getBigDecimalDataType(int precision, int scale, boolean nullable) {
        return getBigDecimalDataType(nullable).precision(precision).scale(scale);
    }

    protected abstract DataType<Timestamp> getJOOQTimestampDataType();

    @Override
    public DataType<Timestamp> getTimestampDataType() {
        return getJOOQTimestampDataType();
    }

    @Override
    public DataType<Timestamp> getTimestampDataType(boolean nullable) {
        return getTimestampDataType().nullable(nullable);
    }

    protected abstract DataType<String> getJOOQClobDataType();

    @Override
    public DataType<String> getClobDataType() {
        return getJOOQClobDataType();
    }

    protected abstract DataType<byte[]> getJOOQBlobDataType();

    @Override
    public DataType<byte[]> getBlobDataType() {
        return getJOOQBlobDataType();
    }

    @Override
    public <T> DataType<T> getDataType(Class<T> type) {
        if (String.class.isAssignableFrom(type)) {
            return (DataType<T>) getStringDataType();
        } else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            return (DataType<T>) getLongDataType();
        } else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            return (DataType<T>) getIntegerDataType();
        } else if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
            return (DataType<T>) getBooleanDataType();
        } else if (BigDecimal.class.isAssignableFrom(type)) {
            return (DataType<T>) getBigDecimalDataType();
        } else if (Date.class.isAssignableFrom(type)) {
            return (DataType<T>) getTimestampDataType();
        }

        throw new IllegalArgumentException(type.getName() + " not supported");
    }

}
