package org.cheeryworks.liteql.database.embedded;

import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.sql.Driver;

public final class HSQLEmbeddedDatabaseConfigurer extends AbstractEmbeddedDatabaseConfigurer {

    @Nullable
    private static HSQLEmbeddedDatabaseConfigurer instance;

    private final Class<? extends Driver> driverClass;

    public static synchronized HSQLEmbeddedDatabaseConfigurer getInstance() {
        try {
            if (instance == null) {
                instance = new HSQLEmbeddedDatabaseConfigurer(
                        (Class<? extends Driver>) ClassUtils.forName(
                                "org.hsqldb.jdbcDriver",
                                HSQLEmbeddedDatabaseConfigurer.class.getClassLoader()));
            }
            return instance;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    private HSQLEmbeddedDatabaseConfigurer(Class<? extends Driver> driverClass) {
        this.driverClass = driverClass;
    }

    @Override
    public void configureConnectionProperties(
            ConnectionProperties properties, String databaseName, String username, String password) {
        properties.setDriverClass(this.driverClass);
        properties.setUrl("jdbc:hsqldb:mem:" + databaseName);
        properties.setUsername(username);
        properties.setPassword(password);
    }

}
