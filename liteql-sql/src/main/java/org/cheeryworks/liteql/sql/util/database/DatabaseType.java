package org.cheeryworks.liteql.sql.util.database;

import java.sql.Driver;

public interface DatabaseType {

    Class<? extends Driver> getDriverClass();

    String getValidationQuery();

}
