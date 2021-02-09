package org.cheeryworks.liteql.jooq.component;

import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqFlywayMigration;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqFlywayMigrationTransactionController;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;

public class SpringJooqFlywayMigrationTransactionController
        implements JooqFlywayMigrationTransactionController {

    @Override
    @Transactional
    public void migrate(JooqFlywayMigration migration, DSLContext dslContext) throws Exception {
        migration.migrate(dslContext);
    }

}
