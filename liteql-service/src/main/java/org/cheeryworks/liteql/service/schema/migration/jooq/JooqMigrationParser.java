package org.cheeryworks.liteql.service.schema.migration.jooq;

import org.apache.commons.collections4.MapUtils;
import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.migration.Migration;
import org.cheeryworks.liteql.schema.migration.operation.CreateFieldMigrationOperation;
import org.cheeryworks.liteql.schema.migration.operation.CreateIndexMigrationOperation;
import org.cheeryworks.liteql.schema.migration.operation.CreateTypeMigrationOperation;
import org.cheeryworks.liteql.schema.migration.operation.CreateUniqueMigrationOperation;
import org.cheeryworks.liteql.schema.migration.operation.DropFieldMigrationOperation;
import org.cheeryworks.liteql.schema.migration.operation.DropIndexMigrationOperation;
import org.cheeryworks.liteql.schema.migration.operation.DropTypeMigrationOperation;
import org.cheeryworks.liteql.schema.migration.operation.DropUniqueMigrationOperation;
import org.cheeryworks.liteql.schema.migration.operation.MigrationOperation;
import org.cheeryworks.liteql.service.jooq.AbstractJooqParser;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.schema.migration.SqlMigrationParser;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.jooq.AlterTableFinalStep;
import org.jooq.CreateTableColumnStep;
import org.jooq.DSLContext;
import org.jooq.DropTableFinalStep;
import org.jooq.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JooqMigrationParser extends AbstractJooqParser implements SqlMigrationParser {

    public JooqMigrationParser(
            LiteQLProperties liteQLProperties, SchemaService schemaService,
            DSLContext dslContext, SqlCustomizer sqlCustomizer) {
        super(liteQLProperties, schemaService, dslContext, sqlCustomizer);
    }

    @Override
    public List<String> migrationsToSql(String schemaName) {
        List<String> migrationsInSql = new ArrayList<>();

        Map<TypeName, Map<String, Migration>> migrations = getSchemaService().getMigrations(schemaName);

        if (MapUtils.isNotEmpty(migrations)) {
            for (Map.Entry<TypeName, Map<String, Migration>> migrationsEntry : migrations.entrySet()) {
                for (Map.Entry<String, Migration> migrationOfDomainType : migrationsEntry.getValue().entrySet()) {
                    migrationsInSql.addAll(migrationToSql(migrationOfDomainType.getValue()));
                }
            }
        }

        return migrationsInSql;
    }

    @Override
    public List<String> migrationToSql(Migration migration) {
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

        String tableName = getSqlCustomizer().getTableName(domainTypeName);

        CreateTableColumnStep createTableColumnStep = getDslContext()
                .createTable(tableName);

        for (Field field : getJooqFields(createTypeMigrationOperation.getFields())) {
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

        String tableName = getSqlCustomizer().getTableName(domainTypeName);

        List<org.jooq.Field> jooqFields = getJooqFields(createFieldMigrationOperation.getFields());

        for (org.jooq.Field jooqField : jooqFields) {
            AlterTableFinalStep alterTableFinalStep = getDslContext()
                    .alterTable(tableName)
                    .addColumn(jooqField, jooqField.getDataType());

            operationsInSql.add(alterTableFinalStep.getSQL());
        }

        return operationsInSql;
    }

    private List<String> parsingDropTypeOperation(
            TypeName typeName, DropTypeMigrationOperation dropTypeMigrationOperation) {
        List<String> operationsInSql = new ArrayList<>();

        String tableName = getSqlCustomizer().getTableName(typeName);

        DomainType domainType = getSchemaService().getDomainType(typeName);

        operationsInSql.addAll(
                parsingIndexMigrationOperation(
                        typeName, new DropUniqueMigrationOperation(domainType.getUniques())));

        operationsInSql.addAll(
                parsingIndexMigrationOperation(
                        typeName, new DropIndexMigrationOperation(domainType.getIndexes())));

        DropTableFinalStep dropTableFinalStep = getDslContext()
                .dropTable(tableName)
                .cascade();

        operationsInSql.add(dropTableFinalStep.getSQL());

        return operationsInSql;
    }

    private List<String> parsingDropFieldOperation(
            TypeName domainTypeName, DropFieldMigrationOperation dropFieldMigrationOperation) {
        List<String> operationsInSql = new ArrayList<>();

        String tableName = getSqlCustomizer().getTableName(domainTypeName);

        for (String field : dropFieldMigrationOperation.getFields()) {
            AlterTableFinalStep alterTableFinalStep = getDslContext()
                    .alterTable(tableName)
                    .dropColumn(getSqlCustomizer().getColumnName(domainTypeName, field));

            operationsInSql.add(alterTableFinalStep.getSQL());
        }

        return operationsInSql;
    }

}
