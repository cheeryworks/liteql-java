package org.cheeryworks.liteql.service.util.database;

public class HSQLDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "org.hsqldb.jdbcDriver";
    }

}
