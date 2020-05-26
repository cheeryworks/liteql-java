package org.cheeryworks.liteql.sql.jooq;

import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.migration.Migration;
import org.cheeryworks.liteql.model.type.migration.MigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateFieldOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateTypeOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateUniqueOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DeleteFieldOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DeleteTypeOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DeleteUniqueOperation;
import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.sql.enums.Database;
import org.cheeryworks.liteql.sql.type.migration.SqlSchemaMigrationParser;
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
        List<String> migrationsInSql = new ArrayList<>();

        for (Migration migration : getRepository().getMigrations(schemaName)) {
            migrationsInSql.addAll(migrationToSql(schemaName, migration));
        }

        return migrationsInSql;
    }

    @Override
    public List<String> migrationToSql(String schemaName, Migration migration) {
        List<String> migrationInSql = new ArrayList<>();

        for (MigrationOperation operation : migration.getOperations()) {
            if (operation instanceof CreateTypeOperation) {
                migrationInSql.addAll(
                        parsingCreateTypeOperation(migration.getDomainType(), (CreateTypeOperation) operation));
            } else if (operation instanceof CreateFieldOperation) {
                migrationInSql.addAll(
                        parsingCreateFieldOperation(migration.getDomainType(), (CreateFieldOperation) operation));
            } else if (operation instanceof CreateUniqueOperation) {
                migrationInSql.addAll(
                        parsingCreateUniqueOperation(migration.getDomainType(), (CreateUniqueOperation) operation));
            } else if (operation instanceof DeleteTypeOperation) {
                migrationInSql.addAll(
                        parsingDeleteTypeOperation(migration.getDomainType(), (DeleteTypeOperation) operation));
            } else if (operation instanceof DeleteFieldOperation) {
                migrationInSql.addAll(
                        parsingDeleteFieldOperation(migration.getDomainType(), (DeleteFieldOperation) operation));
            } else if (operation instanceof DeleteUniqueOperation) {
                migrationInSql.addAll(
                        parsingDeleteUniqueOperation(migration.getDomainType(), (DeleteUniqueOperation) operation));
            }
        }

        return migrationInSql;
    }

    private List<String> parsingCreateTypeOperation(String domainTypeName, CreateTypeOperation createTypeOperation) {
        List<String> operationsInSql = new ArrayList<>();

        String tableName = getTableName(domainTypeName);

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

    private List<String> parsingCreateFieldOperation(
            String domainTypeName, CreateFieldOperation createFieldOperation) {
        List<String> operationsInSql = new ArrayList<>();

        String tableName = getTableName(domainTypeName);

        List<org.jooq.Field> jooqFields = getJooqFields(createFieldOperation.getFields(), getDatabase());

        for (org.jooq.Field jooqField : jooqFields) {
            AlterTableFinalStep alterTableFinalStep = getDslContext()
                    .alterTable(tableName)
                    .addColumn(jooqField, jooqField.getDataType());

            operationsInSql.add(alterTableFinalStep.getSQL());
        }

        return operationsInSql;
    }

    private List<String> parsingCreateUniqueOperation(
            String domainTypeName, CreateUniqueOperation createUniqueOperation) {
        List<String> operationsInSql = new ArrayList<>();

        String tableName = getTableName(domainTypeName);

        operationsInSql.addAll(
                parsingAddUniques(
                        tableName,
                        createUniqueOperation.getUniques()));

        return operationsInSql;
    }

    private List<String> parsingDeleteTypeOperation(String domainTypeName, DeleteTypeOperation deleteTypeOperation) {
        List<String> operationsInSql = new ArrayList<>();

        String tableName = getTableName(domainTypeName);

        DomainType domainType = getRepository().getDomainType(domainTypeName);

        operationsInSql.addAll(parsingDropUniques(tableName, domainType.getUniques()));

        DropTableFinalStep dropTableFinalStep = getDslContext()
                .dropTable(tableName)
                .cascade();

        operationsInSql.add(dropTableFinalStep.getSQL());

        return operationsInSql;
    }

    private List<String> parsingDeleteFieldOperation(
            String domainTypeName, DeleteFieldOperation deleteFieldOperation) {
        List<String> operationsInSql = new ArrayList<>();

        String tableName = getTableName(domainTypeName);

        for (String field : deleteFieldOperation.getFields()) {
            AlterTableFinalStep alterTableFinalStep = getDslContext()
                    .alterTable(tableName)
                    .dropColumn(SqlQueryServiceUtil.getColumnNameByFieldName(field));

            operationsInSql.add(alterTableFinalStep.getSQL());
        }

        return operationsInSql;
    }

    private List<String> parsingDeleteUniqueOperation(
            String domainTypeName, DeleteUniqueOperation deleteUniqueOperation) {
        List<String> operationsInSql = new ArrayList<>();

        String tableName = getTableName(domainTypeName);

        operationsInSql.addAll(parsingDropUniques(tableName, deleteUniqueOperation.getUniques()));

        return operationsInSql;
    }

}
