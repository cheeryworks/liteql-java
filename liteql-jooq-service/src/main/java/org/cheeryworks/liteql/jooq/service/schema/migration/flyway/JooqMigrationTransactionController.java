package org.cheeryworks.liteql.jooq.service.schema.migration.flyway;

import org.jooq.DSLContext;

public interface JooqMigrationTransactionController {

    void migrate(JooqMigration migration, DSLContext dslContext) throws Exception;

}
