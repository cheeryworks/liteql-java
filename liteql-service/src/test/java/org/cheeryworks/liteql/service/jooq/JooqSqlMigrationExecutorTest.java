package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.AbstractSqlTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class JooqSqlMigrationExecutorTest extends AbstractSqlTest {

    private JooqSqlMigrationExecutor jooqSqlSchemaMigrationExecutor;

    public JooqSqlMigrationExecutorTest() {
        super();

        jooqSqlSchemaMigrationExecutor = new JooqSqlMigrationExecutor(getDslContext());
    }

    @Test
    public void testingMigrate() throws SQLException, IOException {
//        jooqSqlSchemaMigrationExecutor.migrate();

        exportAndPrintDdl();
    }

}
