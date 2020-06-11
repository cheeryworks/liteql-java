package org.cheeryworks.liteql.sql.util.database;

public class MariaDBDatabase extends AbstractDatabaseType {

    @Override
    public final String getClassName() {
        return "org.mariadb.jdbc.Driver";
    }

}
