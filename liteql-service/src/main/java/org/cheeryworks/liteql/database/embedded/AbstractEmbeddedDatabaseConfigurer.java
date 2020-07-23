package org.cheeryworks.liteql.database.embedded;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

abstract class AbstractEmbeddedDatabaseConfigurer implements EmbeddedDatabaseConfigurer {

    protected final Logger logger = LoggerFactory.getLogger(AbstractEmbeddedDatabaseConfigurer.class);


    @Override
    public void shutdown(DataSource dataSource, String databaseName) {
        Connection con = null;
        try {
            con = dataSource.getConnection();
            if (con != null) {
                try (Statement stmt = con.createStatement()) {
                    stmt.execute("SHUTDOWN");
                }
            }
        } catch (SQLException ex) {
            logger.info("Could not shut down embedded database", ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Throwable ex) {
                    logger.debug("Could not close JDBC Connection on shutdown", ex);
                }
            }
        }
    }

}
