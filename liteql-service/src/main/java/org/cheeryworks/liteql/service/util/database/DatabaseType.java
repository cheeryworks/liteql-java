package org.cheeryworks.liteql.service.util.database;

import java.sql.Driver;

public interface DatabaseType {

    Class<? extends Driver> getDriverClass();

    String getValidationQuery();

}
