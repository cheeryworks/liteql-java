package org.cheeryworks.liteql.sql.util.database;

public class HSQLDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "org.hsqldb.jdbcDriver";
    }

}
