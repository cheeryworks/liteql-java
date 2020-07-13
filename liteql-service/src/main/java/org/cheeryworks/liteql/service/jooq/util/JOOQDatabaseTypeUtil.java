package org.cheeryworks.liteql.service.jooq.util;

import org.cheeryworks.liteql.service.enums.Database;
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

    public static Database getDatabase(SQLDialect dialect) {
        if (dialect.equals(SQLDialect.H2)) {
            return Database.H2;
        }

        if (dialect.equals(SQLDialect.HSQLDB)) {
            return Database.HSQL;
        }

        if (dialect.equals(SQLDialect.MYSQL)) {
            return Database.MYSQL;
        }

        if (dialect.equals(SQLDialect.MARIADB)) {
            return Database.MARIA_DB;
        }

        if (dialect.equals(SQLDialect.POSTGRES)) {
            return Database.POSTGRESQL;
        }

        return null;
    }

}
