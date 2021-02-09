package org.cheeryworks.liteql.jooq.service.schema.migration.flyway.internal;

import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqFlywayMigration;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqFlywayMigrationTransactionController;
import org.flywaydb.core.api.executor.Context;
import org.flywaydb.core.api.executor.MigrationExecutor;
import org.jooq.DSLContext;

import java.sql.SQLException;

public class JooqFlywayMigrationExecutor implements MigrationExecutor {

    private JooqFlywayMigration migration;

    private DSLContext dslContext;

    private JooqFlywayMigrationTransactionController transactionController;

    public JooqFlywayMigrationExecutor(
            JooqFlywayMigration migration, DSLContext dslContext,
            JooqFlywayMigrationTransactionController transactionController) {
        this.migration = migration;
        this.dslContext = dslContext;
        this.transactionController = transactionController;
    }

    public void setMigration(JooqFlywayMigration migration) {
        this.migration = migration;
    }

    @Override
    public void execute(Context context) throws SQLException {
        try {
            transactionController.migrate(migration, dslContext);
        } catch (Exception e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    @Override
    public boolean canExecuteInTransaction() {
        return true;
    }

    @Override
    public boolean shouldExecute() {
        return true;
    }

}
