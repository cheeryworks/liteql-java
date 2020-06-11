package org.cheeryworks.liteql.sql.jooq;

import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.sql.enums.Database;
import org.cheeryworks.liteql.sql.jooq.util.JOOQDatabaseTypeUtil;
import org.jooq.DSLContext;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.impl.DefaultDSLContext;

import javax.sql.DataSource;

public abstract class AbstractJooqSqlExecutor {

    private DSLContext dslContext;

    private Database database;

    public DSLContext getDslContext() {
        return dslContext;
    }

    public Database getDatabase() {
        return database;
    }

    public AbstractJooqSqlExecutor(DataSource dataSource, Database database) {
        this.database = database;

        Settings settings = SettingsTools.defaultSettings();
        settings.setRenderQuotedNames(RenderQuotedNames.NEVER);
        settings.setRenderNameCase(RenderNameCase.LOWER);

        if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
            settings.withRenderFormatted(true);
        }

        this.dslContext = new DefaultDSLContext(dataSource, JOOQDatabaseTypeUtil.getSqlDialect(database), settings);
    }

}
