package org.cheeryworks.liteql.service.type.migration;

import org.cheeryworks.liteql.model.type.migration.Migration;

import java.util.List;

public interface SqlSchemaMigrationParser {

    List<String> migrationsToSql(String schemaName);

    List<String> migrationToSql(String schemaName, Migration migration);

}
