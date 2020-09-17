package org.cheeryworks.liteql.service.jooq;

import org.apache.commons.lang3.ArrayUtils;
import org.cheeryworks.liteql.service.query.jooq.JooqQueryParser;
import org.cheeryworks.liteql.service.sql.AbstractSqlTest;
import org.cheeryworks.liteql.util.JooqUtil;
import org.jooq.DSLContext;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.impl.DefaultDSLContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractJooqTest extends AbstractSqlTest {

    private DSLContext dslContext;

    private JooqQueryParser jooqQueryParser;

    public DSLContext getDslContext() {
        return dslContext;
    }

    public JooqQueryParser getJooqQueryParser() {
        return jooqQueryParser;
    }

    public AbstractJooqTest() {
        super();

        Settings settings = SettingsTools.defaultSettings();
        settings.setRenderQuotedNames(RenderQuotedNames.NEVER);

        if (getLiteQLProperties().isDiagnosticEnabled()) {
            settings.withRenderFormatted(true);
        }

        this.dslContext = new DefaultDSLContext(
                getDataSource(), JooqUtil.getSqlDialect(getDatabase()), settings);

        this.jooqQueryParser = new JooqQueryParser(
                getLiteQLProperties(), getSchemaService(), getSqlCustomizer(), getDslContext());

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
            connection = getDataSource().getConnection();

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

}
