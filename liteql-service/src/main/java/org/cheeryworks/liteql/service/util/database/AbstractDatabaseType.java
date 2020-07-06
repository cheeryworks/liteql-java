package org.cheeryworks.liteql.service.util.database;

import org.cheeryworks.liteql.model.util.ClassUtil;

import java.sql.Driver;

public abstract class AbstractDatabaseType implements DatabaseType {

    @Override
    public Class<? extends Driver> getDriverClass() {
        return (Class<? extends Driver>) ClassUtil.getClass(getClassName());
    }

    abstract String getClassName();

    @Override
    public String getValidationQuery() {
        return "select 1";
    }

}
