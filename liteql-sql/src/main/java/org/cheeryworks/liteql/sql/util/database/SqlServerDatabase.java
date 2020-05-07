package org.cheeryworks.liteql.sql.util.database;

public class SqlServerDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

}
