package org.cheeryworks.liteql.service.schema.migration;

import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.service.sql.SqlExecutor;

import java.util.List;

public interface SqlMigrationExecutor extends SqlExecutor {

    String MIGRATION_HISTORY_TABLE_SUFFIX = "_migration_history";

    String getLatestMigrationVersion(String migrationHistoryTableName, TypeName domainTypeName);

    void migrate(String schema, TypeName domainTypeName, String version, String description, List<String> sqls);

}
