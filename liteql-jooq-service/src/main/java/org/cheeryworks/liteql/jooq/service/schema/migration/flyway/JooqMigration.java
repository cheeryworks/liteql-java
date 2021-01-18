package org.cheeryworks.liteql.jooq.service.schema.migration.flyway;

import org.jooq.DSLContext;

import java.sql.SQLException;

public interface JooqMigration {

    String getDescription();

    void migrate(DSLContext dslContext) throws SQLException;

}
