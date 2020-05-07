package org.cheeryworks.liteql.sql.jooq.datatype;

import org.jooq.DataType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class SqlServerJOOQDataType extends AbstractJOOQDataType {

    @Override
    protected DataType<String> getJOOQStringDataType() {
//        return SqlServerDataType.VARCHAR;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<Long> getJOOQLongDataType() {
//        return SqlServerDataType.BIGINT;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<Integer> getJOOQIntegerDataType() {
//        return SqlServerDataType.INTEGER;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<Boolean> getJOOQBooleanDataType() {
//        return SqlServerDataType.BOOLEAN;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<BigDecimal> getJOOQBigDecimalDataType() {
//        return SqlServerDataType.DECIMAL;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<Timestamp> getJOOQTimestampDataType() {
//        return SqlServerDataType.TIMESTAMP;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<String> getJOOQClobDataType() {
//        return SqlServerDataType.LONGTEXT;
        throw new UnsupportedOperationException();
    }

    @Override
    protected DataType<byte[]> getJOOQBlobDataType() {
//        return SqlServerDataType.LONGBLOB;
        throw new UnsupportedOperationException();
    }

}
