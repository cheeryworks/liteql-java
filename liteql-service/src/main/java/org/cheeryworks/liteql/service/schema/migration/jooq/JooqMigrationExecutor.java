package org.cheeryworks.liteql.service.schema.migration.jooq;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.schema.migration.Migration;
import org.cheeryworks.liteql.service.jooq.AbstractJooqExecutor;
import org.cheeryworks.liteql.service.schema.migration.SqlMigrationExecutor;
import org.cheeryworks.liteql.util.JooqUtil;
import org.jooq.AlterTableFinalStep;
import org.jooq.CreateTableFinalStep;
import org.jooq.DSLContext;
import org.jooq.InsertFinalStep;
import org.jooq.UpdateFinalStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.cheeryworks.liteql.util.DatabaseUtil.PRIMARY_KEY_PREFIX;
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
    public String getLatestMigrationVersion(String schemaVersionTableName) {
        try {
            String version = (String) getDslContext()
                    .select(max(field("version")))
                    .from(schemaVersionTableName)
                    .fetchOne(0);

            return version;
        } catch (Exception ex) {
            logger.info("Initializing schema version table " + schemaVersionTableName);

            CreateTableFinalStep createTableFinalStep = getDslContext().createTable(schemaVersionTableName)
                    .column("version", JooqUtil.getStringDataType(false, 32))
                    .column("description", JooqUtil.getStringDataType(true, 1000))
                    .column("state", JooqUtil.getStringDataType(false, 30));

            if (getLiteQLProperties().isDiagnosticEnabled()) {
                logger.info(createTableFinalStep.getSQL());
            }

            createTableFinalStep.execute();

            AlterTableFinalStep alterTableFinalStep = getDslContext()
                    .alterTable(schemaVersionTableName).add(
                            constraint(PRIMARY_KEY_PREFIX + schemaVersionTableName)
                                    .primaryKey("version"));

            if (getLiteQLProperties().isDiagnosticEnabled()) {
                logger.info(alterTableFinalStep.getSQL());
            }

            alterTableFinalStep.execute();

            logger.info("Schema version table " + schemaVersionTableName + " is ready");
        }

        return null;
    }

    @Override
    public void migrate(String schema, String version, String description, List<String> sqls) {
        String schemaVersionTableName = getSchemaVersionTableName(schema);

        try {
            InsertFinalStep insertFinalStep = getDslContext().insertInto(table(schemaVersionTableName))
                    .columns(field("version"), field("description"), field("state"))
                    .values(version, description, Migration.STATE_PENDING);

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

            UpdateFinalStep updateFinalStep = getDslContext().update(table(schemaVersionTableName))
                    .set(field("state"), Migration.STATE_SUCCESS);

            if (getLiteQLProperties().isDiagnosticEnabled()) {
                logger.info(updateFinalStep.getSQL());
                logger.info(Arrays.toString(updateFinalStep.getBindValues().toArray()));
            }

            updateFinalStep.execute();
        } catch (Exception ex) {
            UpdateFinalStep updateFinalStep = getDslContext().update(table(schemaVersionTableName))
                    .set(field("state"), Migration.STATE_FAILED);

            if (getLiteQLProperties().isDiagnosticEnabled()) {
                logger.info(updateFinalStep.getSQL());
                logger.info(Arrays.toString(updateFinalStep.getBindValues().toArray()));
            }

            updateFinalStep.execute();

            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private String getSchemaVersionTableName(String schema) {
        return schema + SCHEMA_VERSION_TABLE_SUFFIX;
    }

}
