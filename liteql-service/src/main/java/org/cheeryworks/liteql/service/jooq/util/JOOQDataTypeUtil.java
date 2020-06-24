package org.cheeryworks.liteql.service.jooq.util;


import org.cheeryworks.liteql.service.enums.Database;
import org.cheeryworks.liteql.service.jooq.datatype.H2JOOQDataType;
import org.cheeryworks.liteql.service.jooq.datatype.HSQLJOOQDataType;
import org.cheeryworks.liteql.service.jooq.datatype.JOOQDataType;
import org.cheeryworks.liteql.service.jooq.datatype.MySQLJOOQDataType;
import org.cheeryworks.liteql.service.jooq.datatype.PostgreSQLJOOQDataType;
import org.jooq.SQLDialect;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class JOOQDataTypeUtil {

    private static final JOOQDataType H2_JOOQ_DATA_TYPE = new H2JOOQDataType();

    private static final JOOQDataType HSQL_JOOQ_DATA_TYPE = new HSQLJOOQDataType();

    private static final JOOQDataType MY_SQL_JOOQ_DATA_TYPE = new MySQLJOOQDataType();

    private static final JOOQDataType POSTGRE_SQL_JOOQ_DATA_TYPE = new PostgreSQLJOOQDataType();

    public static final Map<Integer, Class> SUPPORTED_DATA_TYPES;

    static {
        Map<Integer, Class> supportedDataTypes = new HashMap<>();
        supportedDataTypes.put(Types.VARCHAR, String.class);
        supportedDataTypes.put(Types.INTEGER, Integer.class);
        supportedDataTypes.put(Types.BOOLEAN, Boolean.class);
        supportedDataTypes.put(Types.NUMERIC, BigDecimal.class);
        supportedDataTypes.put(Types.TIMESTAMP, Timestamp.class);
        supportedDataTypes.put(Types.CLOB, String.class);
        supportedDataTypes.put(Types.BLOB, byte[].class);

        SUPPORTED_DATA_TYPES = Collections.unmodifiableMap(supportedDataTypes);
    }

    public static JOOQDataType getInstance(Database database) {
        SQLDialect sqlDialect = JOOQDatabaseTypeUtil.getSqlDialect(database.name());

        if (SQLDialect.H2.equals(sqlDialect)) {
            return H2_JOOQ_DATA_TYPE;
        } else if (SQLDialect.HSQLDB.equals(sqlDialect)) {
            return HSQL_JOOQ_DATA_TYPE;
        } else if (SQLDialect.MYSQL.equals(sqlDialect)) {
            return MY_SQL_JOOQ_DATA_TYPE;
        } else if (SQLDialect.POSTGRES.equals(sqlDialect)) {
            return POSTGRE_SQL_JOOQ_DATA_TYPE;
        }

        throw new IllegalArgumentException("Database " + database.name() + " not supported");
    }

}
