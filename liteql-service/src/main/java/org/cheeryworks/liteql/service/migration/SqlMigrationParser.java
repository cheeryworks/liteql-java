package org.cheeryworks.liteql.service.migration;

import org.cheeryworks.liteql.model.type.migration.Migration;
import org.cheeryworks.liteql.service.sql.SqlParser;

import java.util.List;

public interface SqlMigrationParser extends SqlParser {

    List<String> migrationsToSql(String schemaName);

    List<String> migrationToSql(Migration migration);

}
