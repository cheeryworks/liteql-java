package org.cheeryworks.liteql.service.schema.migration;

import org.cheeryworks.liteql.service.sql.SqlExecutor;

import java.util.List;

public interface SqlMigrationExecutor extends SqlExecutor {

    String SCHEMA_VERSION_TABLE_SUFFIX = "_schema_version";

    String getLatestMigrationVersion(String schemaVersionTableName);

    void migrate(String schema, String version, String description, List<String> sqls);

}
