package org.cheeryworks.liteql.jooq.util;

import org.cheeryworks.liteql.skeleton.enums.Database;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.DropSequenceFinalStep;
import org.jooq.DropTableFinalStep;
import org.jooq.impl.DSL;

import java.util.Map;

import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.sequence;
import static org.jooq.impl.DSL.table;

public abstract class JooqDdlUtil {

    public static final String PRIMARY_KEY_PREFIX = "PK_";
    public static final String INDEX_PREFIX = "IDX_";
    public static final String UNIQUE_PREFIX = "UK_";
    public static final String SEQUENCE_PREFIX = "S_";
    public static final String SEQUENCE_TABLE_COLUMN_NAME = "next_val";

    public static void createSequence(String sequenceName, DSLContext dslContext) {
        createSequence(sequenceName, 1, dslContext);
    }

    public static void createSequence(String sequenceName, int incrementSize, DSLContext dslContext) {
        Database database = JooqUtil.getDatabase(dslContext.dialect());

        if (database.equals(Database.MYSQL)) {
            dslContext
                    .createTable(sequenceName)
                    .column(SEQUENCE_TABLE_COLUMN_NAME, JooqUtil.getLongDataType())
                    .execute();

            dslContext
                    .insertInto(table(sequenceName))
                    .columns(field(SEQUENCE_TABLE_COLUMN_NAME, JooqUtil.getLongDataType()))
                    .values(1L)
                    .execute();
        } else {
            dslContext
                    .createSequence(sequence(name(sequenceName))).incrementBy(incrementSize)
                    .execute();
        }
    }

    public static void dropSequence(String sequenceName, DSLContext dslContext) {
        Database database = JooqUtil.getDatabase(dslContext.dialect());

        if (database.equals(Database.MYSQL)) {
            DropTableFinalStep dropTableFinalStep = dslContext
                    .dropTable(sequenceName);

            dslContext.execute(dropTableFinalStep.getSQL());
        } else {
            DropSequenceFinalStep dropSequenceFinalStep = dslContext
                    .dropSequence(sequence(name(sequenceName)));

            dslContext.execute(dropSequenceFinalStep.getSQL());
        }
    }

    public static void dropTable(String tableName, DSLContext dslContext) {
        DropTableFinalStep dropTableFinalStep = dslContext
                .dropTable(tableName);

        dslContext.execute(dropTableFinalStep.getSQL());
    }

    public static void dropForeignKey(String tableName, String constraintName, DSLContext dslContext) {
        dslContext
                .alterTable(tableName).dropForeignKey(constraintName)
                .execute();
    }

    public static void dropIndex(String tableName, String indexName, DSLContext dslContext) {
        Database database = JooqUtil.getDatabase(dslContext.dialect());

        if (database.equals(Database.MYSQL) || database.equals(Database.MARIADB)) {
            dslContext
                    .dropIndex(indexName).on(tableName)
                    .execute();
        } else {
            dslContext
                    .dropIndex(indexName)
                    .execute();
        }
    }

    public static Object getBooleanValue(Database database, boolean value) {
        return value;
    }

    public static void changePrimaryKeyTypeFromLongToString(DSLContext dslContext, String tableName) {
        dslContext
                .alterTable(tableName)
                .dropPrimaryKey(JooqDdlUtil.PRIMARY_KEY_PREFIX + tableName)
                .execute();

        changeFieldTypeFromLongToString(dslContext, tableName, "ID", false);

        dslContext
                .alterTable(tableName)
                .add(constraint(PRIMARY_KEY_PREFIX + tableName).primaryKey(field("ID")))
                .execute();
    }

    public static void changeFieldTypeFromLongToString(DSLContext dslContext, String tableName, String columnName) {
        changeFieldTypeFromLongToString(dslContext, tableName, columnName, true);
    }

    public static void changeFieldTypeFromLongToString(
            DSLContext dslContext, String tableName, String columnName, boolean nullable) {
        Database database = JooqUtil.getDatabase(dslContext.dialect());

        dslContext
                .alterTable(tableName)
                .addColumn(columnName + "_tmp", JooqUtil.getStringDataType())
                .execute();

        dslContext
                .update(table(tableName))
                .set(
                        field(columnName + "_tmp", JooqUtil.getStringDataType()),
                        DSL.cast(field(columnName, JooqUtil.getLongDataType()), JooqUtil.getStringDataType()))
                .execute();

        dslContext
                .alterTable(tableName)
                .dropColumn(columnName)
                .execute();

        dslContext
                .alterTable(tableName)
                .addColumn(columnName, JooqUtil.getStringDataType())
                .execute();

        dslContext
                .update(table(tableName))
                .set(field(columnName, JooqUtil.getStringDataType()),
                        field(columnName + "_tmp", JooqUtil.getStringDataType()))
                .execute();

        if (database.equals(Database.POSTGRESQL) || database.equals(Database.HSQL)) {
            if (!nullable) {
                dslContext.execute("alter table " + tableName + " alter " + columnName + " set not null");
            }
        } else {
            DataType<String> dataType = JooqUtil.getStringDataType();

            if (!nullable) {
                dataType = dataType.nullable(false);
            }

            dslContext
                    .alterTable(tableName)
                    .alterColumn(columnName).set(dataType)
                    .execute();
        }

        dslContext
                .alterTable(tableName)
                .dropColumn(columnName + "_tmp")
                .execute();
    }

    public static <F, T> void changeFieldName(
            DSLContext dslContext, String tableName,
            String oldColumnName, Class<F> oldColumnDataType, String newColumnName, Class<T> newColumnDataType) {
        boolean exist = false;

        Map<String, Object> anyResult = dslContext.select().from(tableName).fetchAnyMap();

        for (String columnName : anyResult.keySet()) {
            if (columnName.equalsIgnoreCase(oldColumnName)) {
                exist = true;
                break;
            }
        }

        if (exist) {
            dslContext
                    .alterTable(tableName)
                    .addColumn(newColumnName, JooqUtil.getDataType(newColumnDataType))
                    .execute();

            dslContext
                    .update(table(tableName))
                    .set(field(newColumnName, newColumnDataType),
                            field(oldColumnName, oldColumnDataType).cast(newColumnDataType))
                    .execute();

            dslContext
                    .alterTable(tableName)
                    .dropColumn(oldColumnName)
                    .execute();
        }
    }

}
