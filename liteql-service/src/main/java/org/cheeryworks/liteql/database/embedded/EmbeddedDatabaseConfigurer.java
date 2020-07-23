package org.cheeryworks.liteql.database.embedded;

import org.springframework.jdbc.datasource.embedded.ConnectionProperties;

import javax.sql.DataSource;

public interface EmbeddedDatabaseConfigurer {

    void configureConnectionProperties(
            ConnectionProperties properties, String databaseName, String username, String password);

    void shutdown(DataSource dataSource, String databaseName);

}
