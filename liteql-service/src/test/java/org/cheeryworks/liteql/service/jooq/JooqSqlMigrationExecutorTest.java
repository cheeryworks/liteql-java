package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.AbstractDatabaseTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class JooqSqlMigrationExecutorTest extends AbstractDatabaseTest {

    private JooqSqlMigrationExecutor jooqSqlSchemaMigrationExecutor;

    public JooqSqlMigrationExecutorTest() {
        super();

        jooqSqlSchemaMigrationExecutor = new JooqSqlMigrationExecutor(
                getRepository(), getDataSource(), getDatabase(), null);
    }

    @Test
    public void testingMigrate() throws SQLException, IOException {
        jooqSqlSchemaMigrationExecutor.migrate();

        exportAndPrintDdl();
    }

}
