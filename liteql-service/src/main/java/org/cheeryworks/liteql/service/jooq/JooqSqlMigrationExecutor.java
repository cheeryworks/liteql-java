package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.model.type.migration.Migration;
import org.cheeryworks.liteql.model.util.LiteQLConstants;
import org.cheeryworks.liteql.service.jooq.datatype.JOOQDataType;
import org.cheeryworks.liteql.service.jooq.util.JOOQDDLUtil;
import org.cheeryworks.liteql.service.migration.SqlMigrationExecutor;
import org.jooq.AlterTableFinalStep;
import org.jooq.CreateTableFinalStep;
import org.jooq.DSLContext;
import org.jooq.InsertFinalStep;
import org.jooq.UpdateFinalStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.table;

public class JooqSqlMigrationExecutor extends AbstractJooqSqlExecutor implements SqlMigrationExecutor {

    private static Logger logger = LoggerFactory.getLogger(JooqSqlMigrationExecutor.class);

    public JooqSqlMigrationExecutor(DSLContext dslContext) {
        super(dslContext);
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
                    .column("version", JOOQDataType.getStringDataType(false, 32))
                    .column("description", JOOQDataType.getStringDataType(true, 1000))
                    .column("state", JOOQDataType.getStringDataType(false, 30));

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

    @Override
    public void migrate(String schema, String version, String description, List<String> sqls) {
        String schemaVersionTableName = getSchemaVersionTableName(schema);

        try {
            InsertFinalStep insertFinalStep = getDslContext().insertInto(table(schemaVersionTableName))
                    .columns(field("version"), field("description"), field("state"))
                    .values(version, description, Migration.STATE_PENDING);

            if (LiteQLConstants.DIAGNOSTIC_ENABLED) {
                logger.info(insertFinalStep.getSQL());
                logger.info(Arrays.toString(insertFinalStep.getBindValues().toArray()));
            }

            insertFinalStep.execute();

            for (String sql : sqls) {
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

    private String getSchemaVersionTableName(String schema) {
        return schema + SCHEMA_VERSION_TABLE_SUFFIX;
    }

}
