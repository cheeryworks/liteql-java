package org.cheeryworks.liteql.sql.jooq;

import org.cheeryworks.liteql.model.migration.Migration;
import org.cheeryworks.liteql.model.migration.MigrationOperation;
import org.cheeryworks.liteql.model.migration.operation.CreateFieldOperation;
import org.cheeryworks.liteql.model.migration.operation.CreateTypeOperation;
import org.cheeryworks.liteql.model.migration.operation.CreateUniqueOperation;
import org.cheeryworks.liteql.model.migration.operation.DeleteFieldOperation;
import org.cheeryworks.liteql.model.migration.operation.DeleteTypeOperation;
import org.cheeryworks.liteql.model.migration.operation.DeleteUniqueOperation;
import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.sql.enums.Database;
import org.cheeryworks.liteql.sql.migration.SqlSchemaMigrationParser;
import org.cheeryworks.liteql.sql.util.SqlQueryServiceUtil;
import org.jooq.AlterTableFinalStep;
import org.jooq.CreateTableColumnStep;
import org.jooq.DropTableFinalStep;
import org.jooq.Field;

import java.util.ArrayList;
import java.util.List;

public class JooqSqlSchemaMigrationParser extends AbstractJooqSqlParser implements SqlSchemaMigrationParser {

    public JooqSqlSchemaMigrationParser(Repository repository, Database database) {
        super(repository, database);
    }

    @Override
    public List<String> migrationsToSql(String schemaName) {
        List<String> migrationsInSql = new ArrayList<String>();

        for (Migration migration : getRepository().getMigrations(schemaName)) {
            migrationsInSql.addAll(migrationToSql(schemaName, migration));
        }

        return migrationsInSql;
    }

    @Override
    public List<String> migrationToSql(String schemaName, Migration migration) {
        List<String> migrationInSql = new ArrayList<String>();

        for (MigrationOperation operation : migration.getOperations()) {
            if (operation instanceof CreateTypeOperation) {
                migrationInSql.addAll(parsingCreateTypeOperation(schemaName, (CreateTypeOperation) operation));
            } else if (operation instanceof CreateFieldOperation) {
                migrationInSql.addAll(parsingCreateFieldOperation(schemaName, (CreateFieldOperation) operation));
            } else if (operation instanceof CreateUniqueOperation) {
                migrationInSql.addAll(parsingCreateUniqueOperation(schemaName, (CreateUniqueOperation) operation));
            } else if (operation instanceof DeleteTypeOperation) {
                migrationInSql.addAll(parsingDeleteTypeOperation(schemaName, (DeleteTypeOperation) operation));
            } else if (operation instanceof DeleteFieldOperation) {
                migrationInSql.addAll(parsingDeleteFieldOperation(schemaName, (DeleteFieldOperation) operation));
            } else if (operation instanceof DeleteUniqueOperation) {
                migrationInSql.addAll(parsingDeleteUniqueOperation(schemaName, (DeleteUniqueOperation) operation));
            }
        }

        return migrationInSql;
    }

    private List<String> parsingCreateTypeOperation(String schemaName, CreateTypeOperation createTypeOperation) {
        List<String> operationsInSql = new ArrayList<String>();

        String tableName = getTableName(schemaName, createTypeOperation.getDomainType());

        CreateTableColumnStep createTableColumnStep = getDslContext()
                .createTable(tableName);

        for (Field field : getJooqFields(createTypeOperation.getFields(), getDatabase())) {
            if (createTableColumnStep == null) {
                createTableColumnStep = createTableColumnStep.column(field, field.getDataType());
            } else {
                createTableColumnStep.column(field, field.getDataType());
            }
        }

        operationsInSql.add(createTableColumnStep.getSQL());

        operationsInSql.add(parsingAddPrimaryKey(tableName));

        operationsInSql.addAll(parsingAddUniques(tableName, createTypeOperation.getUniques()));

        return operationsInSql;
    }

    private List<String> parsingCreateFieldOperation(String schemaName, CreateFieldOperation createFieldOperation) {
        List<String> operationsInSql = new ArrayList<String>();

        String tableName = getTableName(schemaName, createFieldOperation.getDomainType());

        List<org.jooq.Field> jooqFields = getJooqFields(createFieldOperation.getFields(), getDatabase());

        for (org.jooq.Field jooqField : jooqFields) {
            AlterTableFinalStep alterTableFinalStep = getDslContext()
                    .alterTable(tableName)
                    .addColumn(jooqField, jooqField.getDataType());

            operationsInSql.add(alterTableFinalStep.getSQL());
        }

        return operationsInSql;
    }

    private List<String> parsingCreateUniqueOperation(String schemaName, CreateUniqueOperation createUniqueOperation) {
        List<String> operationsInSql = new ArrayList<String>();

        String tableName = getTableName(schemaName, createUniqueOperation.getDomainType());

        operationsInSql.addAll(
                parsingAddUniques(
                        tableName,
                        createUniqueOperation.getUniques()));

        return operationsInSql;
    }

    private List<String> parsingDeleteTypeOperation(String schemaName, DeleteTypeOperation deleteTypeOperation) {
        List<String> operationsInSql = new ArrayList<String>();

        String tableName = getTableName(schemaName, deleteTypeOperation.getDomainType());

        DomainType domainType = getRepository().getDomainType(schemaName, deleteTypeOperation.getDomainType());

        operationsInSql.addAll(parsingDropUniques(tableName, domainType.getUniques()));

        DropTableFinalStep dropTableFinalStep = getDslContext()
                .dropTable(tableName)
                .cascade();

        operationsInSql.add(dropTableFinalStep.getSQL());

        return operationsInSql;
    }

    private List<String> parsingDeleteFieldOperation(String schemaName, DeleteFieldOperation deleteFieldOperation) {
        List<String> operationsInSql = new ArrayList<String>();

        String tableName = getTableName(schemaName, deleteFieldOperation.getDomainType());

        for (String field : deleteFieldOperation.getFields()) {
            AlterTableFinalStep alterTableFinalStep = getDslContext()
                    .alterTable(tableName)
                    .dropColumn(SqlQueryServiceUtil.getColumnNameByFieldName(field));

            operationsInSql.add(alterTableFinalStep.getSQL());
        }

        return operationsInSql;
    }

    private List<String> parsingDeleteUniqueOperation(String schemaName, DeleteUniqueOperation deleteUniqueOperation) {
        List<String> operationsInSql = new ArrayList<String>();

        String tableName = getTableName(schemaName, deleteUniqueOperation.getDomainType());

        operationsInSql.addAll(parsingDropUniques(tableName, deleteUniqueOperation.getUniques()));

        return operationsInSql;
    }

}
