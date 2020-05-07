package org.cheeryworks.liteql.sql.util.database;

public class PostgreSQLDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "org.postgresql.Driver";
    }

}
