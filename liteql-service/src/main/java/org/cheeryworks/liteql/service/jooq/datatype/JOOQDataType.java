package org.cheeryworks.liteql.service.jooq.datatype;

import org.jooq.DataType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface JOOQDataType {

    int STRING_DEFAULT_LENGTH = 255;

    int STRING_MAX_LENGTH = 4000;

    int LONG_LENGTH = 19;

    int INTEGER_MAX_LENGTH = 10;

    int BIG_DECIMAL_PRECISION = 19;

    int BIG_DECIMAL_MIN_SCALE = 2;

    int BIG_DECIMAL_MAX_SCALE = 6;

    DataType<String> getStringDataType();

    DataType<String> getStringDataType(boolean nullable);

    DataType<String> getStringDataType(int length, boolean nullable);

    DataType<Long> getLongDataType();

    DataType<Long> getLongDataType(boolean nullable);

    DataType<Long> getLongDataType(int length, boolean nullable);

    DataType<Integer> getIntegerDataType();

    DataType<Integer> getIntegerDataType(boolean nullable);

    DataType<Integer> getIntegerDataType(int length, boolean nullable);

    DataType<Boolean> getBooleanDataType();

    DataType<BigDecimal> getBigDecimalDataType();

    DataType<BigDecimal> getBigDecimalDataType(boolean nullable);

    DataType<BigDecimal> getBigDecimalDataType(int precision, int scale, boolean nullable);

    DataType<Timestamp> getTimestampDataType();

    DataType<Timestamp> getTimestampDataType(boolean nullable);

    DataType<String> getClobDataType();

    DataType<String> getClobDataType(boolean nullable);

    DataType<byte[]> getBlobDataType();

    DataType<byte[]> getBlobDataType(boolean nullable);

    <T> DataType<T> getDataType(Class<T> type);

}
