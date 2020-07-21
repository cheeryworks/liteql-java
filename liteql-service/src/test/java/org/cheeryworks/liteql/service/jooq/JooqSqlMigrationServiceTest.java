package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.service.migration.jooq.JooqSqlMigrationService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class JooqSqlMigrationServiceTest extends AbstractJooqSqlTest {

    private JooqSqlMigrationService jooqSqlMigrationService;

    public JooqSqlMigrationServiceTest() {
        super();

        jooqSqlMigrationService = new JooqSqlMigrationService(getRepository(), getDslContext(), null);
    }

    @Test
    public void testingMigrate() throws SQLException, IOException {
        jooqSqlMigrationService.migrate();

        exportAndPrintDdl();
    }

}
