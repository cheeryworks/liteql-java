package org.cheeryworks.liteql.skeleton.service.schema.migration;

import org.cheeryworks.liteql.skeleton.schema.migration.Migration;
import org.cheeryworks.liteql.skeleton.service.sql.SqlParser;

import java.util.List;

public interface SqlMigrationParser extends SqlParser {

    List<String> migrationsToSql(String schemaName);

    List<String> migrationToSql(Migration migration);

}
