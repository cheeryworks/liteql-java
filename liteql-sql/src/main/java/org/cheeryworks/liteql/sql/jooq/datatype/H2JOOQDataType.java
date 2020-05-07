package org.cheeryworks.liteql.sql.jooq.datatype;

import org.jooq.DataType;
import org.jooq.util.hsqldb.HSQLDBDataType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class H2JOOQDataType extends AbstractJOOQDataType {

    @Override
    protected DataType<String> getJOOQStringDataType() {
        return HSQLDBDataType.VARCHAR;
    }

    @Override
    protected DataType<Long> getJOOQLongDataType() {
        return HSQLDBDataType.BIGINT;
    }

    @Override
    protected DataType<Integer> getJOOQIntegerDataType() {
        return HSQLDBDataType.INTEGER;
    }

    @Override
    protected DataType<Boolean> getJOOQBooleanDataType() {
        return HSQLDBDataType.BOOLEAN;
    }

    @Override
    protected DataType<BigDecimal> getJOOQBigDecimalDataType() {
        return HSQLDBDataType.NUMERIC;
    }

    @Override
    protected DataType<Timestamp> getJOOQTimestampDataType() {
        return HSQLDBDataType.TIMESTAMP;
    }

    @Override
    protected DataType<String> getJOOQClobDataType() {
        return HSQLDBDataType.CLOB;
    }

    @Override
    protected DataType<byte[]> getJOOQBlobDataType() {
        return HSQLDBDataType.BLOB;
    }

}
