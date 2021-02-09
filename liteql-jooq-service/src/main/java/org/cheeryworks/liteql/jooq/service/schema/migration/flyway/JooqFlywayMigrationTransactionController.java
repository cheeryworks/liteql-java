package org.cheeryworks.liteql.jooq.service.schema.migration.flyway;

import org.jooq.DSLContext;

public interface JooqFlywayMigrationTransactionController {

    void migrate(JooqFlywayMigration migration, DSLContext dslContext) throws Exception;

}
