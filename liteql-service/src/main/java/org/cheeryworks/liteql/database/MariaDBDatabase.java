package org.cheeryworks.liteql.database;

public class MariaDBDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "org.mariadb.jdbc.Driver";
    }

}
