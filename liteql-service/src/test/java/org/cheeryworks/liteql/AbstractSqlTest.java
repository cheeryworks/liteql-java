package org.cheeryworks.liteql;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.enums.Database;
import org.cheeryworks.liteql.service.jooq.util.JOOQDatabaseTypeUtil;
import org.cheeryworks.liteql.service.repository.PathMatchingResourceRepository;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Script;
import org.jooq.DSLContext;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.impl.DefaultDSLContext;

import javax.sql.DataSource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public abstract class AbstractSqlTest extends AbstractTest {

    private Repository repository;

    private Database database;

    private String databaseName;

    private String databaseUrl;

    private DataSource dataSource;

    private DSLContext dslContext;

    public Repository getRepository() {
        return repository;
    }

    public Database getDatabase() {
        return database;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public DSLContext getDslContext() {
        return dslContext;
    }

    public AbstractSqlTest() {
        repository = new PathMatchingResourceRepository(getObjectMapper(), "classpath*:/liteql");

        database = Database.H2;

        databaseName = UUID.randomUUID().toString().substring(0, 6);

        databaseUrl = "jdbc:h2:mem:" + databaseName
                + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

        JdbcDataSource h2DataSource = new JdbcDataSource();
        h2DataSource.setURL(databaseUrl);

        dataSource = h2DataSource;

        Settings settings = SettingsTools.defaultSettings();
        settings.setRenderQuotedNames(RenderQuotedNames.NEVER);
        settings.setRenderNameCase(RenderNameCase.LOWER);

        if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
            settings.withRenderFormatted(true);
        }

        this.dslContext = new DefaultDSLContext(
                dataSource, JOOQDatabaseTypeUtil.getSqlDialect(getDatabase()), settings);

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
