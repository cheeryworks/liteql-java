package org.cheeryworks.liteql.jooq.component;

import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqMigration;
import org.cheeryworks.liteql.jooq.service.schema.migration.flyway.JooqMigrationTransactionController;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;

public class SpringJooqMigrationTransactionController
        implements JooqMigrationTransactionController {

    @Override
    @Transactional
    public void migrate(JooqMigration migration, DSLContext dslContext) throws Exception {
        migration.migrate(dslContext);
    }

}
