package org.cheeryworks.liteql.service.jooq;

import org.apache.commons.lang3.ArrayUtils;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.service.AbstractSqlTest;
import org.cheeryworks.liteql.service.jooq.util.JOOQDatabaseTypeUtil;
import org.jooq.DSLContext;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.impl.DefaultDSLContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractJooqSqlTest extends AbstractSqlTest {


    private DSLContext dslContext;

    public DSLContext getDslContext() {
        return dslContext;
    }

    public AbstractJooqSqlTest() {
        super();

        Settings settings = SettingsTools.defaultSettings();
        settings.setRenderQuotedNames(RenderQuotedNames.NEVER);
        settings.setRenderNameCase(RenderNameCase.LOWER);

        if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
            settings.withRenderFormatted(true);
        }

        this.dslContext = new DefaultDSLContext(
                getDataSource(), JOOQDatabaseTypeUtil.getSqlDialect(getDatabase()), settings);

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
