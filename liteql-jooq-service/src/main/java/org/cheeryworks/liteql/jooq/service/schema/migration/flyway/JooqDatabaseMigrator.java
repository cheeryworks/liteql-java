package org.cheeryworks.liteql.jooq.service.schema.migration.flyway;

public interface JooqDatabaseMigrator {

    void migrate();

    void clean();

}
