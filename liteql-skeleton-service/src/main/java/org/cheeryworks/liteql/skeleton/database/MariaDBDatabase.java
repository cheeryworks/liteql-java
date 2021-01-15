package org.cheeryworks.liteql.skeleton.database;

public class MariaDBDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "org.mariadb.jdbc.Driver";
    }

}
