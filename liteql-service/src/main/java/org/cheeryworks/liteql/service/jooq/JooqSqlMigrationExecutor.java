package org.cheeryworks.liteql.service.jooq;

import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.type.migration.Migration;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.service.Repository;
import org.cheeryworks.liteql.service.SqlCustomizer;
import org.cheeryworks.liteql.service.enums.Database;
import org.cheeryworks.liteql.service.jooq.datatype.JOOQDataType;
import org.cheeryworks.liteql.service.jooq.util.JOOQDDLUtil;
import org.cheeryworks.liteql.service.jooq.util.JOOQDataTypeUtil;
import org.cheeryworks.liteql.service.migration.SqlMigrationExecutor;
import org.cheeryworks.liteql.service.migration.SqlMigrationParser;
import org.cheeryworks.liteql.service.util.DatabaseTypeUtil;
import org.jooq.AlterTableFinalStep;
import org.jooq.CreateTableFinalStep;
import org.jooq.InsertFinalStep;
import org.jooq.UpdateFinalStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Arrays;

import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.table;

public class JooqSqlMigrationExecutor extends AbstractJooqSqlExecutor implements SqlMigrationExecutor {

    private static final String SCHEMA_VERSION_TABLE_SUFFIX = "_schema_version";

    private static Logger logger = LoggerFactory.getLogger(JooqSqlMigrationExecutor.class);

    private Repository repository;

    private SqlMigrationParser sqlMigrationParser;

    public JooqSqlMigrationExecutor(
            Repository repository, DataSource dataSource, Database database, SqlCustomizer sqlCustomizer) {
        super(dataSource, database);

        this.repository = repository;
        this.sqlMigrationParser = new JooqSqlMigrationParser(repository, database, sqlCustomizer);
    }

    @Override
    public void migrate() {
        isDatabaseReady();

        for (String schema : this.repository.getSchemaNames()) {

            String schemaVersionTableName = schema + SCHEMA_VERSION_TABLE_SUFFIX;

            String version = checkAndCreateSchemaVersionTable(schemaVersionTableName);

            logger.info("Migrating schema " + schema + "...");

            for (Migration migration : this.repository.getMigrations(schema)) {
                try {
                    if (StringUtils.isBlank(version) || migration.getVersion().compareTo(version) > 0) {
                        InsertFinalStep insertFinalStep = getDslContext().insertInto(table(schemaVersionTableName))
                                .columns(field("version"), field("description"), field("state"))
                                .values(migration.getVersion(), migration.getDescription(), Migration.STATE_PENDING);

                        if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
                            logger.info(insertFinalStep.getSQL());
                            logger.info(Arrays.toString(insertFinalStep.getBindValues().toArray()));
                        }

                        insertFinalStep.execute();

                        for (String sql : sqlMigrationParser.migrationToSql(schema, migration)) {
                            getDslContext().execute(sql);

                            if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
                                logger.info(sql);
                            }
                        }

                        UpdateFinalStep updateFinalStep = getDslContext().update(table(schemaVersionTableName))
                                .set(field("state"), Migration.STATE_SUCCESS);

                        if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
                            logger.info(updateFinalStep.getSQL());
                            logger.info(Arrays.toString(updateFinalStep.getBindValues().toArray()));
                        }

                        updateFinalStep.execute();
                    }
                } catch (Exception ex) {
                    UpdateFinalStep updateFinalStep = getDslContext().update(table(schemaVersionTableName))
                            .set(field("state"), Migration.STATE_FAILED);

                    if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
                        logger.info(updateFinalStep.getSQL());
                        logger.info(Arrays.toString(updateFinalStep.getBindValues().toArray()));
                    }

                    updateFinalStep.execute();

                    throw new RuntimeException(ex.getMessage(), ex);
                }
            }

            logger.info("Migrating schema " + schema + " finished successfully");
        }
    }

    private void isDatabaseReady() {
        getDslContext().fetch(DatabaseTypeUtil.getInstance(getDatabase()).getValidationQuery());

        logger.info("Database is ready");
    }

    private String checkAndCreateSchemaVersionTable(String schemaVersionTableName) {
        try {
            String version = (String) getDslContext()
                    .select(max(field("version")))
                    .from(schemaVersionTableName)
                    .fetchOne(0);

            return version;
        } catch (Exception ex) {
            logger.info("Initializing schema version table " + schemaVersionTableName);

            JOOQDataType jooqDataType = JOOQDataTypeUtil.getInstance(getDatabase());

            CreateTableFinalStep createTableFinalStep = getDslContext().createTable(schemaVersionTableName)
                    .column("version", jooqDataType.getStringDataType().length(32).nullable(false))
                    .column("description", jooqDataType.getStringDataType().length(1000))
                    .column("state", jooqDataType.getStringDataType().length(30));

            if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
                logger.info(createTableFinalStep.getSQL());
            }

            createTableFinalStep.execute();

            AlterTableFinalStep alterTableFinalStep = getDslContext()
                    .alterTable(schemaVersionTableName).add(
                            constraint(JOOQDDLUtil.PRIMARY_KEY_PREFIX + schemaVersionTableName)
                                    .primaryKey("version"));

            if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
                logger.info(alterTableFinalStep.getSQL());
            }

            alterTableFinalStep.execute();

            logger.info("Schema version table " + schemaVersionTableName + " is ready");
        }

        return null;
    }

}
