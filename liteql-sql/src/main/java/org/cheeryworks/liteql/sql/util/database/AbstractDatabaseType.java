package org.cheeryworks.liteql.sql.util.database;

import java.sql.Driver;

public abstract class AbstractDatabaseType implements DatabaseType {

    @Override
    public Class<? extends Driver> getDriverClass() {
        try {
            return (Class<? extends Driver>) Class.forName(getClassName());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    abstract String getClassName();

    @Override
    public String getValidationQuery() {
        return "select 1";
    }

}
