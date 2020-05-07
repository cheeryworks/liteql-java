package org.cheeryworks.liteql.sql.migration;

import org.cheeryworks.liteql.model.migration.Migration;

import java.util.List;

public interface SqlSchemaMigrationParser {

    List<String> migrationsToSql(String schemaName);

    List<String> migrationToSql(String schemaName, Migration migration);

}
