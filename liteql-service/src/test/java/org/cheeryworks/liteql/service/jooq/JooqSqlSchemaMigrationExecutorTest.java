package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.AbstractDatabaseTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class JooqSqlSchemaMigrationExecutorTest extends AbstractDatabaseTest {

    private JooqSqlSchemaMigrationExecutor jooqSqlSchemaMigrationExecutor;

    public JooqSqlSchemaMigrationExecutorTest() {
        super();

        jooqSqlSchemaMigrationExecutor = new JooqSqlSchemaMigrationExecutor(
                getRepository(), getDataSource(), getDatabase());
    }

    @Test
    public void testingMigrate() throws SQLException, IOException {
        jooqSqlSchemaMigrationExecutor.migrate();

        exportAndPrintDdl();
    }

}
