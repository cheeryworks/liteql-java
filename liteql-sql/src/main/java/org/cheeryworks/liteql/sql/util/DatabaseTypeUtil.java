package org.cheeryworks.liteql.sql.util;

import org.cheeryworks.liteql.sql.enums.Database;
import org.cheeryworks.liteql.sql.util.database.DatabaseType;
import org.cheeryworks.liteql.sql.util.database.H2Database;
import org.cheeryworks.liteql.sql.util.database.HSQLDatabase;
import org.cheeryworks.liteql.sql.util.database.MySQLDatabase;
import org.cheeryworks.liteql.sql.util.database.PostgreSQLDatabase;

public final class DatabaseTypeUtil {

    public static final Database[] SUPPORTED_DATABASES = {
            Database.H2,
            Database.HSQL,
            Database.MYSQL,
            Database.POSTGRESQL
    };

    private static DatabaseType h2Database = new H2Database();

    private static DatabaseType hsqlDatabase = new HSQLDatabase();

    private static DatabaseType mysqlDatabase = new MySQLDatabase();

    private static DatabaseType postgresqlDatabase = new PostgreSQLDatabase();

    public static DatabaseType getInstance(Database database) {
        if (Database.H2.equals(database)) {
            return h2Database;
        } else if (Database.HSQL.equals(database)) {
            return hsqlDatabase;
        } else if (Database.MYSQL.equals(database)) {
            return mysqlDatabase;
        } else if (Database.POSTGRESQL.equals(database)) {
            return postgresqlDatabase;
        }

        return null;
    }

    public static boolean isH2(Database database) {
        if (Database.H2.equals(database)) {
            return true;
        }

        return false;
    }

    public static boolean isHSQL(Database database) {
        if (Database.HSQL.equals(database)) {
            return true;
        }

        return false;
    }

    public static boolean isMySQL(Database database) {
        if (Database.MYSQL.equals(database)) {
            return true;
        }

        return false;
    }

    public static boolean isPostgreSQL(Database database) {
        if (Database.POSTGRESQL.equals(database)) {
            return true;
        }

        return false;
    }

}
