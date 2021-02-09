package org.cheeryworks.liteql.jooq.service.schema.migration.flyway;

public interface JooqFlywayMigrator {

    void migrate();

    void clean();

}
