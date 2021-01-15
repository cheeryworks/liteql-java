package org.cheeryworks.liteql.jooq.service.schema.migration;

import org.cheeryworks.liteql.jooq.service.AbstractJooqExecutor;
import org.cheeryworks.liteql.jooq.util.JooqUtil;
import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.schema.migration.Migration;
import org.cheeryworks.liteql.skeleton.service.schema.migration.SqlMigrationExecutor;
import org.jooq.AlterTableFinalStep;
import org.jooq.CreateTableFinalStep;
import org.jooq.DSLContext;
import org.jooq.InsertFinalStep;
import org.jooq.UpdateFinalStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.cheeryworks.liteql.skeleton.util.DatabaseUtil.PRIMARY_KEY_PREFIX;
import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.table;

public class JooqMigrationExecutor extends AbstractJooqExecutor implements SqlMigrationExecutor {

    private static Logger logger = LoggerFactory.getLogger(JooqMigrationExecutor.class);

    public JooqMigrationExecutor(LiteQLProperties liteQLProperties, DSLContext dslContext) {
        super(liteQLProperties, dslContext);
    }

    @Override
    public String getLatestMigrationVersion(String migrationHistoryTableName, TypeName domainTypeName) {
        try {
            String version = (String) getDslContext()
                    .select(max(field("version")))
                    .from(migrationHistoryTableName)
                    .where(field("domain_type_name").eq(domainTypeName.getFullname()))
                    .fetchOne(0);

            return version;
        } catch (Exception ex) {
            logger.info("Initializing schema version table " + migrationHistoryTableName);

            CreateTableFinalStep createTableFinalStep = getDslContext().createTable(migrationHistoryTableName)
                    .column("domain_type_name", JooqUtil.getStringDataType(false, 255))
                    .column("version", JooqUtil.getStringDataType(false, 32))
                    .column("description", JooqUtil.getStringDataType(true, 1000))
                    .column("state", JooqUtil.getStringDataType(false, 30));

            if (getLiteQLProperties().isDiagnosticEnabled()) {
                logger.info(createTableFinalStep.getSQL());
            }

            createTableFinalStep.execute();

            AlterTableFinalStep alterTableFinalStep = getDslContext()
                    .alterTable(migrationHistoryTableName).add(
                            constraint(PRIMARY_KEY_PREFIX + migrationHistoryTableName)
                                    .primaryKey("domain_type_name", "version"));

            if (getLiteQLProperties().isDiagnosticEnabled()) {
                logger.info(alterTableFinalStep.getSQL());
            }

            alterTableFinalStep.execute();

            logger.info("Schema version table " + migrationHistoryTableName + " is ready");
        }

        return null;
    }

    @Override
    public void migrate(String schema, TypeName domainTypeName, String version, String description, List<String> sqls) {
        String migrationHistoryTableName = getMigrationHistoryTableName(schema);

        try {
            InsertFinalStep insertFinalStep = getDslContext().insertInto(table(migrationHistoryTableName))
                    .columns(field("domain_type_name"), field("version"), field("description"), field("state"))
                    .values(domainTypeName.getFullname(), version, description, Migration.STATE_PENDING);

            if (getLiteQLProperties().isDiagnosticEnabled()) {
                logger.info(insertFinalStep.getSQL());
                logger.info(Arrays.toString(insertFinalStep.getBindValues().toArray()));
            }

            insertFinalStep.execute();

            for (String sql : sqls) {
                getDslContext().execute(sql);

                if (getLiteQLProperties().isDiagnosticEnabled()) {
                    logger.info(sql);
                }
            }

            UpdateFinalStep updateFinalStep = getDslContext().update(table(migrationHistoryTableName))
                    .set(field("state"), Migration.STATE_SUCCESS)
                    .where(
                            field("domain_type_name").eq(domainTypeName.getFullname())
                                    .and(field("version").eq(version)));

            if (getLiteQLProperties().isDiagnosticEnabled()) {
                logger.info(updateFinalStep.getSQL());
                logger.info(Arrays.toString(updateFinalStep.getBindValues().toArray()));
            }

            updateFinalStep.execute();
        } catch (Exception ex) {
            UpdateFinalStep updateFinalStep = getDslContext().update(table(migrationHistoryTableName))
                    .set(field("state"), Migration.STATE_FAILED)
                    .where(
                            field("domain_type_name").eq(domainTypeName.getFullname())
                                    .and(field("version").eq(version)));

            if (getLiteQLProperties().isDiagnosticEnabled()) {
                logger.info(updateFinalStep.getSQL());
                logger.info(Arrays.toString(updateFinalStep.getBindValues().toArray()));
            }

            updateFinalStep.execute();

            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private String getMigrationHistoryTableName(String schema) {
        return schema + MIGRATION_HISTORY_TABLE_SUFFIX;
    }

}
