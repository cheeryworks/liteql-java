package org.cheeryworks.liteql.jooq.service.schema.migration;

import org.cheeryworks.liteql.jooq.service.AbstractJooqTest;
import org.cheeryworks.liteql.skeleton.service.schema.migration.LoggingMigrationEventPublisher;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class JooqMigrationServiceTest extends AbstractJooqTest {

    private JooqMigrationService jooqMigrationService;

    public JooqMigrationServiceTest() {
        super();

        jooqMigrationService = new JooqMigrationService(
                getLiteQLProperties(), getJooqQueryParser(), new LoggingMigrationEventPublisher());
    }

    @Test
    public void testingMigrate() throws SQLException, IOException {
        jooqMigrationService.migrate();

        exportAndPrintDdl();
    }

}
