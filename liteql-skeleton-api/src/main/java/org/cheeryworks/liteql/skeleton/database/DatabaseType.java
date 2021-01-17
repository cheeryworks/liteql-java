package org.cheeryworks.liteql.skeleton.database;

import java.sql.Driver;

public interface DatabaseType {

    Class<? extends Driver> getDriverClass();

    String getValidationQuery();

}
