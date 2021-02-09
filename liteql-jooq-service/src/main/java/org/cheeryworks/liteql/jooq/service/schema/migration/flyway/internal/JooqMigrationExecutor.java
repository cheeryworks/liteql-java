package org.cheeryworks.liteql.jooq.service.schema.migration.flyway.internal;

import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqMigration;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqMigrationTransactionController;
import org.flywaydb.core.api.executor.Context;
import org.flywaydb.core.api.executor.MigrationExecutor;
import org.jooq.DSLContext;

import java.sql.SQLException;

public class JooqMigrationExecutor implements MigrationExecutor {

    private JooqMigration migration;

    private DSLContext dslContext;

    private JooqMigrationTransactionController transactionController;

    public JooqMigrationExecutor(
            JooqMigration migration, DSLContext dslContext,
            JooqMigrationTransactionController transactionController) {
        this.migration = migration;
        this.dslContext = dslContext;
        this.transactionController = transactionController;
    }

    public void setMigration(JooqMigration migration) {
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
