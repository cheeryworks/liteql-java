package org.cheeryworks.liteql.service.schema.migration;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.migration.Migration;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractSqlMigrationService implements MigrationService {

    private static Logger logger = LoggerFactory.getLogger(AbstractSqlMigrationService.class);

    private SchemaService schemaService;

    private SqlMigrationParser sqlMigrationParser;

    private SqlMigrationExecutor sqlMigrationExecutor;

    public AbstractSqlMigrationService(
            SchemaService schemaService,
            SqlMigrationParser sqlMigrationParser,
            SqlMigrationExecutor sqlMigrationExecutor) {
        this.schemaService = schemaService;
        this.sqlMigrationParser = sqlMigrationParser;
        this.sqlMigrationExecutor = sqlMigrationExecutor;
    }

    public SchemaService getSchemaService() {
        return schemaService;
    }

    public SqlMigrationParser getSqlMigrationParser() {
        return sqlMigrationParser;
    }

    public SqlMigrationExecutor getSqlMigrationExecutor() {
        return sqlMigrationExecutor;
    }

    @Override
    public void migrate() {
        sqlMigrationExecutor.isDatabaseReady();

        for (String schema : this.schemaService.getSchemaNames()) {
            Map<TypeName, Map<String, Migration>> migrations = this.schemaService.getMigrations(schema);

            if (MapUtils.isEmpty(migrations)) {
                continue;
            }

            logger.info("Migrating schema " + schema + "...");

            try {
                String schemaVersionTableName = schema + SqlMigrationExecutor.SCHEMA_VERSION_TABLE_SUFFIX;

                String latestVersion = getSqlMigrationExecutor().getLatestMigrationVersion(schemaVersionTableName);

                for (Map.Entry<TypeName, Map<String, Migration>> migrationOfDomainType : migrations.entrySet()) {
                    if (MapUtils.isEmpty(migrationOfDomainType.getValue())) {
                        continue;
                    }

                    Set<String> baselineVersions = new LinkedHashSet<>();

                    migrationOfDomainType.getValue().forEach((name, migration) -> {
                        if (migration.isBaseline()) {
                            baselineVersions.add(migration.getVersion());
                        }
                    });

                    String latestBaselineVersion = baselineVersions.stream().max(String.CASE_INSENSITIVE_ORDER).get();

                    for (Map.Entry<String, Migration> migrationEntry : migrationOfDomainType.getValue().entrySet()) {
                        Migration migration = migrationEntry.getValue();

                        if (StringUtils.isBlank(latestVersion)
                                && migration.getVersion().compareTo(latestBaselineVersion) < 0) {
                            continue;
                        }

                        if (StringUtils.isNotBlank(latestVersion)
                                && (migration.getVersion().compareTo(latestVersion) <= 0 || migration.isBaseline())) {
                            continue;
                        }

                        getSqlMigrationExecutor().migrate(
                                schema, migration.getVersion(), migration.getDescription(),
                                getSqlMigrationParser().migrationToSql(migration));
                    }
                }

                logger.info("Migrating schema " + schema + " finished");
            } catch (Exception ex) {
                throw new IllegalStateException(
                        "Migrating schema " + schema + " finished failed, " + ex.getMessage(), ex);
            }
        }
    }

}
