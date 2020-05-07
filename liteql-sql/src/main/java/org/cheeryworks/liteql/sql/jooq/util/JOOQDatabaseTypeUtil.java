package org.cheeryworks.liteql.sql.jooq.util;

import org.cheeryworks.liteql.sql.enums.Database;
import org.jooq.SQLDialect;
import org.jooq.exception.SQLDialectNotSupportedException;

public class JOOQDatabaseTypeUtil {

    public static SQLDialect getSqlDialect(String databaseType) {
        Database database = Database.valueOf(databaseType);

        return getSqlDialect(database);
    }

    public static SQLDialect getSqlDialect(Database database) {
        if (database.equals(Database.H2)) {
            return SQLDialect.H2;
        }

        if (database.equals(Database.HSQL)) {
            return SQLDialect.HSQLDB;
        }

        if (database.equals(Database.MYSQL)) {
            return SQLDialect.MYSQL;
        }

        if (database.equals(Database.POSTGRESQL)) {
            return SQLDialect.POSTGRES;
        }

        if (database.equals(Database.ORACLE)) {
            return SQLDialect.ORACLE;
        }

        if (database.equals(Database.SQL_SERVER)) {
            return SQLDialect.SQL_SERVER;
        }

        if (database.equals(Database.DB2)) {
            return SQLDialect.DB2;
        }

        throw new SQLDialectNotSupportedException(database.name());
    }

}
