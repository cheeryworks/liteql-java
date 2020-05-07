package org.cheeryworks.liteql.sql.jooq.util;


import org.cheeryworks.liteql.sql.enums.Database;
import org.cheeryworks.liteql.sql.jooq.datatype.DB2JOOQDataType;
import org.cheeryworks.liteql.sql.jooq.datatype.H2JOOQDataType;
import org.cheeryworks.liteql.sql.jooq.datatype.HSQLJOOQDataType;
import org.cheeryworks.liteql.sql.jooq.datatype.JOOQDataType;
import org.cheeryworks.liteql.sql.jooq.datatype.MySQLJOOQDataType;
import org.cheeryworks.liteql.sql.jooq.datatype.OracleJOOQDataType;
import org.cheeryworks.liteql.sql.jooq.datatype.PostgreSQLJOOQDataType;
import org.cheeryworks.liteql.sql.jooq.datatype.SqlServerJOOQDataType;
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

    private static final JOOQDataType ORACLE_JOOQ_DATA_TYPE = new OracleJOOQDataType();

    private static final JOOQDataType SQL_SERVER_JOOQ_DATA_TYPE = new SqlServerJOOQDataType();

    private static final JOOQDataType DB2_JOOQ_DATA_TYPE = new DB2JOOQDataType();

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
        } else if (SQLDialect.ORACLE.equals(sqlDialect)) {
            return ORACLE_JOOQ_DATA_TYPE;
        } else if (SQLDialect.SQL_SERVER.equals(sqlDialect)) {
            return SQL_SERVER_JOOQ_DATA_TYPE;
        } else if (SQLDialect.DB2.equals(sqlDialect)) {
            return DB2_JOOQ_DATA_TYPE;
        }

        throw new IllegalArgumentException("Database " + database.name() + " not supported");
    }

}
