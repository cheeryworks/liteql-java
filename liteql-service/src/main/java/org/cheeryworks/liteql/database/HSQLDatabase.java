package org.cheeryworks.liteql.database;

public class HSQLDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "org.hsqldb.jdbcDriver";
    }

}
