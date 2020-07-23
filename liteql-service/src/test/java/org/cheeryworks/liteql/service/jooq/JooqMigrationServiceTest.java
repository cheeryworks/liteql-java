package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.service.schema.migration.jooq.JooqMigrationService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class JooqMigrationServiceTest extends AbstractJooqSqlTest {

    private JooqMigrationService jooqMigrationService;

    public JooqMigrationServiceTest() {
        super();

        jooqMigrationService = new JooqMigrationService(
                getLiteQLProperties(), getSchemaService(), getDslContext(), null);
    }

    @Test
    public void testingMigrate() throws SQLException, IOException {
        jooqMigrationService.migrate();

        exportAndPrintDdl();
    }

}
