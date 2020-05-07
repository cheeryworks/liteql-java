package org.cheeryworks.liteql.sql.jooq.datatype;

import org.jooq.DataType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class OracleJOOQDataType extends AbstractJOOQDataType {

    @Override
    protected DataType<String> getJOOQStringDataType() {
//        return OracleDataType.VARCHAR;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<Long> getJOOQLongDataType() {
//        return OracleDataType.BIGINT;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<Integer> getJOOQIntegerDataType() {
//        return OracleDataType.INT;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<Boolean> getJOOQBooleanDataType() {
//        DataType dataType = OracleDataType.BOOLEAN;
//        dataType.precision(1).scale(0);
//
//        return dataType;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<BigDecimal> getJOOQBigDecimalDataType() {
//        return OracleDataType.DECIMAL;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<Timestamp> getJOOQTimestampDataType() {
//        return OracleDataType.TIMESTAMP;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<String> getJOOQClobDataType() {
//        return OracleDataType.LONGTEXT;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<byte[]> getJOOQBlobDataType() {
//        return OracleDataType.LONGBLOB;
        throw new UnsupportedOperationException();
    }

}
