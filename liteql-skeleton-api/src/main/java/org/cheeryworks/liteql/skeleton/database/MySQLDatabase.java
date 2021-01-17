package org.cheeryworks.liteql.skeleton.database;

public class MySQLDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "com.mysql.jdbc.Driver";
    }

}
