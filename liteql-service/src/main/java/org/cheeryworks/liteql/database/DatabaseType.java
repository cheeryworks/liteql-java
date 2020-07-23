package org.cheeryworks.liteql.database;

import java.sql.Driver;

public interface DatabaseType {

    Class<? extends Driver> getDriverClass();

    String getValidationQuery();

}
