package org.cheeryworks.liteql.jooq.service.schema.migration.flyway;

import java.util.List;

public interface JooqMigrationDelegate {

    String SCHEMA_VERSION_TABLE_SUFFIX = "flyway_history";

    String getSchemaVersionTablePrefix();

    String getBaselineVersion();

    Class[] getMigrationClasses();

    List<Package> getPreMigratedPackages();

}
