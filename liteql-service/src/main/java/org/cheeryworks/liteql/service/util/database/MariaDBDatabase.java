package org.cheeryworks.liteql.service.util.database;

public class MariaDBDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "org.mariadb.jdbc.Driver";
    }

}
