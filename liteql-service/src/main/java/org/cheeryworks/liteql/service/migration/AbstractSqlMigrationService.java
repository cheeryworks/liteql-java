package org.cheeryworks.liteql.service.migration;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.type.migration.Migration;
import org.cheeryworks.liteql.service.MigrationService;
import org.cheeryworks.liteql.service.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.cheeryworks.liteql.service.migration.SqlMigrationExecutor.SCHEMA_VERSION_TABLE_SUFFIX;

public abstract class AbstractSqlMigrationService implements MigrationService {

    private static Logger logger = LoggerFactory.getLogger(AbstractSqlMigrationService.class);

    private Repository repository;

    private SqlMigrationParser sqlMigrationParser;

    private SqlMigrationExecutor sqlMigrationExecutor;

    public AbstractSqlMigrationService(
            Repository repository,
            SqlMigrationParser sqlMigrationParser,
            SqlMigrationExecutor sqlMigrationExecutor) {
        this.repository = repository;
        this.sqlMigrationParser = sqlMigrationParser;
        this.sqlMigrationExecutor = sqlMigrationExecutor;
    }

    public Repository getRepository() {
        return repository;
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

        for (String schema : this.repository.getSchemaNames()) {
            Map<TypeName, Map<String, Migration>> migrations = this.repository.getMigrations(schema);

            if (MapUtils.isEmpty(migrations)) {
                continue;
            }

            logger.info("Migrating schema " + schema + "...");

            try {
                String schemaVersionTableName = schema + SCHEMA_VERSION_TABLE_SUFFIX;

                String latestVersion = getSqlMigrationExecutor().getLatestMigrationVersion(schemaVersionTableName);

                for (Map.Entry<TypeName, Map<String, Migration>> migrationOfDomainType : migrations.entrySet()) {
                    if (MapUtils.isEmpty(migrationOfDomainType.getValue())) {
                        continue;
                    }

                    for (Map.Entry<String, Migration> migrationEntry : migrationOfDomainType.getValue().entrySet()) {
                        Migration migration = migrationEntry.getValue();

                        if (StringUtils.isBlank(latestVersion) || migration.getVersion().compareTo(latestVersion) > 0) {
                            getSqlMigrationExecutor().migrate(
                                    schema, migration.getVersion(), migration.getDescription(),
                                    getSqlMigrationParser().migrationToSql(migration));
                        }
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
