package org.cheeryworks.liteql.sql.jooq.datatype;

import org.jooq.DataType;
import org.jooq.util.postgres.PostgresDataType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class PostgreSQLJOOQDataType extends AbstractJOOQDataType {

    @Override
    protected DataType<String> getJOOQStringDataType() {
        return PostgresDataType.VARCHAR;
    }

    @Override
    protected DataType<Long> getJOOQLongDataType() {
        return PostgresDataType.INT8;
    }

    @Override
    protected DataType<Integer> getJOOQIntegerDataType() {
        return PostgresDataType.INT4;
    }

    @Override
    protected DataType<Boolean> getJOOQBooleanDataType() {
        return PostgresDataType.BOOLEAN;
    }

    @Override
    protected DataType<BigDecimal> getJOOQBigDecimalDataType() {
        return PostgresDataType.NUMERIC;
    }

    @Override
    protected DataType<Timestamp> getJOOQTimestampDataType() {
        return PostgresDataType.TIMESTAMP;
    }

    @Override
    protected DataType<String> getJOOQClobDataType() {
        return PostgresDataType.TEXT;
    }

    @Override
    protected DataType<byte[]> getJOOQBlobDataType() {
        return PostgresDataType.BYTEA;
    }

}
