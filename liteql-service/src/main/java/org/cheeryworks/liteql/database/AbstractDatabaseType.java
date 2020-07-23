package org.cheeryworks.liteql.database;

import org.cheeryworks.liteql.util.LiteQLUtil;

import java.sql.Driver;

public abstract class AbstractDatabaseType implements DatabaseType {

    @Override
    public Class<? extends Driver> getDriverClass() {
        return (Class<? extends Driver>) LiteQLUtil.getClass(getClassName());
    }

    abstract String getClassName();

    @Override
    public String getValidationQuery() {
        return "select 1";
    }

}