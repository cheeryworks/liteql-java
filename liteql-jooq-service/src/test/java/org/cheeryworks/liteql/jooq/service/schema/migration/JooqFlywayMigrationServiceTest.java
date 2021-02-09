package org.cheeryworks.liteql.jooq.service.schema.migration;

import org.cheeryworks.liteql.jooq.service.AbstractJooqTest;
import org.cheeryworks.liteql.skeleton.event.publisher.schema.migration.LoggingMigrationEventPublisher;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class JooqFlywayMigrationServiceTest extends AbstractJooqTest {

    private JooqMigrationService jooqMigrationService;

    public JooqFlywayMigrationServiceTest() {
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
