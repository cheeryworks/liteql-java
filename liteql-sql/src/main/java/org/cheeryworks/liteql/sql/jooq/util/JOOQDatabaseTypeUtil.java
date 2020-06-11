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

        if (database.equals(Database.MARIA_DB)) {
            return SQLDialect.MARIADB;
        }

        if (database.equals(Database.POSTGRESQL)) {
            return SQLDialect.POSTGRES;
        }

        throw new SQLDialectNotSupportedException(database.name());
    }

}
