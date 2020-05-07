package org.cheeryworks.liteql;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Script;

import javax.sql.DataSource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public abstract class AbstractDatabaseTest extends AbstractSqlTest {

    private String databaseName;

    private String databaseUrl;

    private DataSource dataSource;

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public AbstractDatabaseTest() {
        super();

        databaseName = UUID.randomUUID().toString().substring(0, 6);

        databaseUrl = "jdbc:h2:mem:" + databaseName
                + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

        JdbcDataSource h2DataSource = new JdbcDataSource();
        h2DataSource.setURL(databaseUrl);

        dataSource = h2DataSource;

        initDatabase();
    }

    protected String[] getInitSqls() {
        return new String[0];
    }

    protected void initDatabase() {
        String[] initSqls = getInitSqls();

        if (ArrayUtils.isEmpty(initSqls)) {
            return;
        }

        Connection connection = null;

        try {
            connection = dataSource.getConnection();

            for (String initSql : initSqls) {
                executeSql(connection, initSql);
            }

            connection.commit();
            connection.close();
        } catch (Exception ex) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }

            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private void executeSql(Connection connection, String sql) throws SQLException {
        Statement statement = connection.createStatement();

        statement.execute(sql);
        statement.close();
    }

    protected void exportAndPrintDdl() {
        try {
            String exportFilePath = "/tmp/" + getDatabaseName() + ".sql";

            new Script().runTool(
                    "-url",
                    getDatabaseUrl(),
                    "-script",
                    exportFilePath);

            File exportScriptFile = new File(exportFilePath);

            getLogger().info(IOUtils.toString(exportScriptFile.toURI(), StandardCharsets.UTF_8));

            if (exportScriptFile.exists()) {
                exportScriptFile.delete();
            }
        } catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

}
