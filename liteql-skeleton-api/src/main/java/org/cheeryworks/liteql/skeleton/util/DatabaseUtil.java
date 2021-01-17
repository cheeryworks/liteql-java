package org.cheeryworks.liteql.skeleton.util;

import org.cheeryworks.liteql.skeleton.database.DatabaseType;
import org.cheeryworks.liteql.skeleton.database.H2Database;
import org.cheeryworks.liteql.skeleton.database.HSQLDatabase;
import org.cheeryworks.liteql.skeleton.database.MariaDBDatabase;
import org.cheeryworks.liteql.skeleton.database.MySQLDatabase;
import org.cheeryworks.liteql.skeleton.database.PostgreSQLDatabase;
import org.cheeryworks.liteql.skeleton.enums.Database;

public final class DatabaseUtil {

    public static final String PRIMARY_KEY_PREFIX = "pk_";

    public static final String UNIQUE_KEY_PREFIX = "uk_";

    public static final String INDEX_KEY_PREFIX = "idx_";

    public static final Database[] SUPPORTED_DATABASES = {
            Database.H2,
            Database.HSQL,
            Database.MYSQL,
            Database.MARIADB,
            Database.POSTGRESQL
    };

    private static DatabaseType h2Database = new H2Database();

    private static DatabaseType hsqlDatabase = new HSQLDatabase();

    private static DatabaseType mysqlDatabase = new MySQLDatabase();

    private static DatabaseType mariaDBDatabase = new MariaDBDatabase();

    private static DatabaseType postgresqlDatabase = new PostgreSQLDatabase();

    public static DatabaseType getInstance(Database database) {
        if (Database.H2.equals(database)) {
            return h2Database;
        } else if (Database.HSQL.equals(database)) {
            return hsqlDatabase;
        } else if (Database.MYSQL.equals(database)) {
            return mysqlDatabase;
        } else if (Database.MARIADB.equals(database)) {
            return mariaDBDatabase;
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

    public static boolean isMariaDB(Database database) {
        if (Database.MARIADB.equals(database)) {
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
