package org.cheeryworks.liteql.database.embedded;

import com.fasterxml.uuid.Generators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.DataSourceFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class EmbeddedDatabaseFactory {

    private static Logger logger = LoggerFactory.getLogger(EmbeddedDatabaseFactory.class);

    private String databaseName = Generators.timeBasedGenerator().generate().toString().replaceAll("-", "");

    private String username = "sa";

    private String password = Generators.timeBasedGenerator().generate().toString().replaceAll("-", "");

    private DataSourceFactory dataSourceFactory = new SimpleDriverDataSourceFactory();

    private EmbeddedDatabaseConfigurer databaseConfigurer = HSQLEmbeddedDatabaseConfigurer.getInstance();

    @Nullable
    private DatabasePopulator databasePopulator;

    @Nullable
    private DataSource dataSource;

    public void setDatabaseName(String databaseName) {
        Assert.hasText(databaseName, "Database name is required");
        this.databaseName = databaseName;
    }

    public void setUsername(String username) {
        Assert.hasText(username, "Username is required");
        this.username = username;
    }

    public void setPassword(String password) {
        Assert.hasText(password, "Password is required");
        this.password = password;
    }

    /**
     * Set the strategy that will be used to initialize or populate the embedded
     * database.
     * <p>Defaults to {@code null}.
     */
    public void setDatabasePopulator(DatabasePopulator populator) {
        this.databasePopulator = populator;
    }

    /**
     * Factory method that returns the {@linkplain EmbeddedDatabase embedded database}
     * instance, which is also a {@link DataSource}.
     */
    public EmbeddedDatabase getDatabase() {
        if (this.dataSource == null) {
            initDatabase();
        }
        return new EmbeddedDataSourceProxy(this.dataSource);
    }


    protected void initDatabase() {
        if (this.databaseConfigurer == null) {
            this.databaseConfigurer = HSQLEmbeddedDatabaseConfigurer.getInstance();
        }
        this.databaseConfigurer.configureConnectionProperties(
                this.dataSourceFactory.getConnectionProperties(),
                this.databaseName, this.username, this.password);
        this.dataSource = this.dataSourceFactory.getDataSource();

        if (logger.isInfoEnabled()) {
            if (this.dataSource instanceof SimpleDriverDataSource) {
                SimpleDriverDataSource simpleDriverDataSource = (SimpleDriverDataSource) this.dataSource;
                logger.info(String.format("Starting embedded database: url='%s', username='%s'",
                        simpleDriverDataSource.getUrl(), simpleDriverDataSource.getUsername()));
            } else {
                logger.info(String.format("Starting embedded database '%s'", this.databaseName));
            }
        }

        // Now populate the database
        if (this.databasePopulator != null) {
            try {
                DatabasePopulatorUtils.execute(this.databasePopulator, this.dataSource);
            } catch (RuntimeException ex) {
                // failed to populate, so leave it as not initialized
                shutdownDatabase();
                throw ex;
            }
        }
    }

    public void shutdownDatabase() {
        if (this.dataSource != null) {
            if (logger.isInfoEnabled()) {
                if (this.dataSource instanceof SimpleDriverDataSource) {
                    logger.info(String.format("Shutting down embedded database: url='%s'",
                            ((SimpleDriverDataSource) this.dataSource).getUrl()));
                } else {
                    logger.info(String.format("Shutting down embedded database '%s'", this.databaseName));
                }
            }
            if (this.databaseConfigurer != null) {
                this.databaseConfigurer.shutdown(this.dataSource, this.databaseName);
            }
            this.dataSource = null;
        }
    }

    private class EmbeddedDataSourceProxy implements EmbeddedDatabase {

        private final DataSource dataSource;

        EmbeddedDataSourceProxy(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return this.dataSource.getConnection();
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return this.dataSource.getConnection(username, password);
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return this.dataSource.getLogWriter();
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            this.dataSource.setLogWriter(out);
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return this.dataSource.getLoginTimeout();
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            this.dataSource.setLoginTimeout(seconds);
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return this.dataSource.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return this.dataSource.isWrapperFor(iface);
        }

        // getParentLogger() is required for JDBC 4.1 compatibility
        @Override
        public java.util.logging.Logger getParentLogger() {
            return java.util.logging.Logger.getLogger("");
        }

        @Override
        public void shutdown() {
            shutdownDatabase();
        }
    }

}
