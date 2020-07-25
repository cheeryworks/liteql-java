package org.cheeryworks.liteql.service.sql;

import org.apache.commons.io.IOUtils;
import org.cheeryworks.liteql.AbstractTest;
import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.enums.Database;
import org.cheeryworks.liteql.service.schema.DefaultSchemaService;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Script;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public abstract class AbstractSqlTest extends AbstractTest {

    private Database database;

    private String databaseName;

    private String databaseUrl;

    private DataSource dataSource;

    private LiteQLProperties liteQLProperties;

    private SchemaService schemaService;

    private SqlCustomizer sqlCustomizer;

    public Database getDatabase() {
        return database;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public LiteQLProperties getLiteQLProperties() {
        return liteQLProperties;
    }

    public SchemaService getSchemaService() {
        return schemaService;
    }

    public SqlCustomizer getSqlCustomizer() {
        return sqlCustomizer;
    }

    public AbstractSqlTest() {
        database = Database.H2;

        databaseName = UUID.randomUUID().toString().substring(0, 6);

        databaseUrl = "jdbc:h2:mem:" + databaseName
                + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

        JdbcDataSource h2DataSource = new JdbcDataSource();
        h2DataSource.setURL(databaseUrl);

        dataSource = h2DataSource;

        liteQLProperties = Mockito.mock(LiteQLProperties.class);

        liteQLProperties.setDiagnosticEnabled(true);

        schemaService = new DefaultSchemaService(getLiteQLProperties(), "classpath*:/liteql");

        sqlCustomizer = new DefaultSqlCustomizer();
    }

    protected void exportAndPrintDdl() {
        try {
            String exportFilePath = "/tmp/" + databaseName + ".sql";

            new Script().runTool(
                    "-url",
                    databaseUrl,
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
