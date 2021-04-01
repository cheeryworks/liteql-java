package org.cheeryworks.liteql.skeleton.database;

import org.cheeryworks.liteql.skeleton.util.LiteQL;

import java.sql.Driver;

public abstract class AbstractDatabaseType implements DatabaseType {

    @Override
    public Class<? extends Driver> getDriverClass() {
        return (Class<? extends Driver>) LiteQL.ClassUtils.getClass(getClassName());
    }

    abstract String getClassName();

    @Override
    public String getValidationQuery() {
        return "select 1";
    }

}
