package org.cheeryworks.liteql.skeleton.database;

public class PostgreSQLDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "org.postgresql.Driver";
    }

}
