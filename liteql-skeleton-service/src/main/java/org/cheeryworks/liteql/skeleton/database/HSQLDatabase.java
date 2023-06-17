package org.cheeryworks.liteql.skeleton.database;

public class HSQLDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "org.hsqldb.jdbc.JDBCDriver";
    }

    @Override
    public final String getValidationQuery() {
        return "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS";
    }

}
