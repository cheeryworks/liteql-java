package org.cheeryworks.liteql.sql.jooq.datatype;

import org.jooq.DataType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class DB2JOOQDataType extends AbstractJOOQDataType {

    @Override
    protected DataType<String> getJOOQStringDataType() {
//        return DB2DataType.VARCHAR;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<Long> getJOOQLongDataType() {
//        return DB2DataType.BIGINT;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<Integer> getJOOQIntegerDataType() {
//        return DB2DataType.INTEGER;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<Boolean> getJOOQBooleanDataType() {
//        return DB2DataType.BOOLEAN;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<BigDecimal> getJOOQBigDecimalDataType() {
//        return DB2DataType.DECIMAL;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<Timestamp> getJOOQTimestampDataType() {
//        return DB2DataType.TIMESTAMP;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<String> getJOOQClobDataType() {
//        return DB2DataType.LONGTEXT;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<byte[]> getJOOQBlobDataType() {
//        return DB2DataType.LONGBLOB;
        throw new UnsupportedOperationException();
    }

}
