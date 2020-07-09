package org.cheeryworks.liteql.service.jooq.datatype;

import org.jooq.DataType;
import org.jooq.util.mariadb.MariaDBDataType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class MariaDBJOOQDataType extends AbstractJOOQDataType {

    @Override
    protected DataType<String> getJOOQStringDataType() {
        return MariaDBDataType.VARCHAR;
    }

    @Override
    protected DataType<Long> getJOOQLongDataType() {
        return MariaDBDataType.BIGINT;
    }

    @Override
    protected DataType<Integer> getJOOQIntegerDataType() {
        return MariaDBDataType.INTEGER;
    }

    @Override
    protected DataType<Boolean> getJOOQBooleanDataType() {
        return MariaDBDataType.BIT;
    }

    @Override
    protected DataType<BigDecimal> getJOOQBigDecimalDataType() {
        return MariaDBDataType.DECIMAL;
    }

    @Override
    protected DataType<Timestamp> getJOOQTimestampDataType() {
        return MariaDBDataType.DATETIME;
    }

    @Override
    protected DataType<String> getJOOQClobDataType() {
        return MariaDBDataType.LONGTEXT;
    }

    @Override
    protected DataType<byte[]> getJOOQBlobDataType() {
        return MariaDBDataType.LONGBLOB;
    }

}
