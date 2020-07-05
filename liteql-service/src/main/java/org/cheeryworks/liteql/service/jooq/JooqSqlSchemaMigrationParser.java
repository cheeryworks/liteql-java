package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.model.type.migration.Migration;
import org.cheeryworks.liteql.model.type.migration.operation.CreateFieldMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateIndexMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateTypeMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateUniqueMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DropFieldMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DropIndexMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DropTypeMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.DropUniqueMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.MigrationOperation;
import org.cheeryworks.liteql.service.enums.Database;
import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.service.type.migration.SqlSchemaMigrationParser;
import org.cheeryworks.liteql.service.util.SqlQueryServiceUtil;
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
            if (operation instanceof CreateTypeMigrationOperation) {
                migrationInSql.addAll(
                        parsingCreateTypeOperation(
                                migration.getDomainTypeName(), (CreateTypeMigrationOperation) operation));
            } else if (operation instanceof CreateFieldMigrationOperation) {
                migrationInSql.addAll(
                        parsingCreateFieldOperation(
                                migration.getDomainTypeName(), (CreateFieldMigrationOperation) operation));
            } else if (operation instanceof CreateUniqueMigrationOperation) {
                migrationInSql.addAll(
                        parsingIndexMigrationOperation(
                                migration.getDomainTypeName(), (CreateUniqueMigrationOperation) operation));
            } else if (operation instanceof CreateIndexMigrationOperation) {
                migrationInSql.addAll(
                        parsingIndexMigrationOperation(
                                migration.getDomainTypeName(), (CreateIndexMigrationOperation) operation));
            } else if (operation instanceof DropTypeMigrationOperation) {
                migrationInSql.addAll(
                        parsingDropTypeOperation(
                                migration.getDomainTypeName(), (DropTypeMigrationOperation) operation));
            } else if (operation instanceof DropFieldMigrationOperation) {
                migrationInSql.addAll(
                        parsingDropFieldOperation(
                                migration.getDomainTypeName(), (DropFieldMigrationOperation) operation));
            } else if (operation instanceof DropUniqueMigrationOperation) {
                migrationInSql.addAll(
                        parsingIndexMigrationOperation(
                                migration.getDomainTypeName(), (DropUniqueMigrationOperation) operation));
            } else if (operation instanceof DropIndexMigrationOperation) {
                migrationInSql.addAll(
                        parsingIndexMigrationOperation(
                                migration.getDomainTypeName(), (DropIndexMigrationOperation) operation));
            }
        }

        return migrationInSql;
    }

    private List<String> parsingCreateTypeOperation(
            TypeName domainTypeName, CreateTypeMigrationOperation createTypeMigrationOperation) {
        List<String> operationsInSql = new ArrayList<>();

        String tableName = getTableName(domainTypeName.getFullname());

        CreateTableColumnStep createTableColumnStep = getDslContext()
                .createTable(tableName);

        for (Field field : getJooqFields(createTypeMigrationOperation.getFields(), getDatabase())) {
            if (createTableColumnStep == null) {
                createTableColumnStep = createTableColumnStep.column(field, field.getDataType());
            } else {
                createTableColumnStep.column(field, field.getDataType());
            }
        }

        operationsInSql.add(createTableColumnStep.getSQL());

        operationsInSql.add(parsingAddPrimaryKey(tableName));

        operationsInSql.addAll(
                parsingIndexMigrationOperation(
                        domainTypeName, new CreateUniqueMigrationOperation(createTypeMigrationOperation.getUniques())));

        operationsInSql.addAll(
                parsingIndexMigrationOperation(
                        domainTypeName, new CreateIndexMigrationOperation(createTypeMigrationOperation.getIndexes())));

        return operationsInSql;
    }

    private List<String> parsingCreateFieldOperation(
            TypeName domainTypeName, CreateFieldMigrationOperation createFieldMigrationOperation) {
        List<String> operationsInSql = new ArrayList<>();

        String tableName = getTableName(domainTypeName.getFullname());

        List<org.jooq.Field> jooqFields = getJooqFields(createFieldMigrationOperation.getFields(), getDatabase());

        for (org.jooq.Field jooqField : jooqFields) {
            AlterTableFinalStep alterTableFinalStep = getDslContext()
                    .alterTable(tableName)
                    .addColumn(jooqField, jooqField.getDataType());

            operationsInSql.add(alterTableFinalStep.getSQL());
        }

        return operationsInSql;
    }

    private List<String> parsingDropTypeOperation(
            TypeName domainTypeName, DropTypeMigrationOperation dropTypeMigrationOperation) {
        List<String> operationsInSql = new ArrayList<>();

        String tableName = getTableName(domainTypeName.getFullname());

        DomainType domainType = getRepository().getDomainType(domainTypeName);

        operationsInSql.addAll(
                parsingIndexMigrationOperation(
                        domainTypeName, new DropUniqueMigrationOperation(domainType.getUniques())));

        operationsInSql.addAll(
                parsingIndexMigrationOperation(
                        domainTypeName, new DropIndexMigrationOperation(domainType.getIndexes())));

        DropTableFinalStep dropTableFinalStep = getDslContext()
                .dropTable(tableName)
                .cascade();

        operationsInSql.add(dropTableFinalStep.getSQL());

        return operationsInSql;
    }

    private List<String> parsingDropFieldOperation(
            TypeName domainTypeName, DropFieldMigrationOperation dropFieldMigrationOperation) {
        List<String> operationsInSql = new ArrayList<>();

        String tableName = getTableName(domainTypeName.getFullname());

        for (String field : dropFieldMigrationOperation.getFields()) {
            AlterTableFinalStep alterTableFinalStep = getDslContext()
                    .alterTable(tableName)
                    .dropColumn(SqlQueryServiceUtil.getColumnNameByFieldName(field));

            operationsInSql.add(alterTableFinalStep.getSQL());
        }

        return operationsInSql;
    }

}
