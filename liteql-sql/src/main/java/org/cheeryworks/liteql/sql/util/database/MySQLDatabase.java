package org.cheeryworks.liteql.sql.util.database;

public class MySQLDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "com.mysql.jdbc.Driver";
    }

}
