package org.cheeryworks.liteql.database;

public class PostgreSQLDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "org.postgresql.Driver";
    }

}
