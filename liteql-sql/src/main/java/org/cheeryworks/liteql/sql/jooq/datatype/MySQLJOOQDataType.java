package org.cheeryworks.liteql.sql.jooq.datatype;

import org.jooq.DataType;
import org.jooq.util.mysql.MySQLDataType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class MySQLJOOQDataType extends AbstractJOOQDataType {

    @Override
    protected DataType<String> getJOOQStringDataType() {
        return MySQLDataType.VARCHAR;
    }

    @Override
    protected DataType<Long> getJOOQLongDataType() {
        return MySQLDataType.BIGINT;
    }

    @Override
    protected DataType<Integer> getJOOQIntegerDataType() {
        return MySQLDataType.INTEGER;
    }

    @Override
    protected DataType<Boolean> getJOOQBooleanDataType() {
        return MySQLDataType.BIT;
    }

    @Override
    protected DataType<BigDecimal> getJOOQBigDecimalDataType() {
        return MySQLDataType.DECIMAL;
    }

    @Override
    protected DataType<Timestamp> getJOOQTimestampDataType() {
        return MySQLDataType.DATETIME;
    }

    @Override
    protected DataType<String> getJOOQClobDataType() {
        return MySQLDataType.LONGTEXT;
    }

    @Override
    protected DataType<byte[]> getJOOQBlobDataType() {
        return MySQLDataType.LONGBLOB;
    }

}
