package org.cheeryworks.liteql.jooq.service.schema.migration;

import org.apache.commons.collections4.MapUtils;
import org.cheeryworks.liteql.jooq.service.AbstractJooqParser;
import org.cheeryworks.liteql.jooq.service.query.JooqQueryParser;
import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.delete.DeleteQuery;
import org.cheeryworks.liteql.skeleton.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.skeleton.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.schema.migration.Migration;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.CreateFieldMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.CreateIndexMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.CreateTypeMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.CreateUniqueMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.DataMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.DropFieldMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.DropIndexMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.DropTypeMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.DropUniqueMigrationOperation;
import org.cheeryworks.liteql.skeleton.schema.migration.operation.MigrationOperation;
import org.cheeryworks.liteql.skeleton.service.schema.migration.SqlMigrationParser;
import org.jooq.AlterTableFinalStep;
import org.jooq.CreateTableColumnStep;
import org.jooq.DropTableFinalStep;
import org.jooq.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JooqMigrationParser extends AbstractJooqParser implements SqlMigrationParser {

    private JooqQueryParser jooqQueryParser;

    public JooqMigrationParser(
            LiteQLProperties liteQLProperties, JooqQueryParser jooqQueryParser) {
        super(
                liteQLProperties,
                jooqQueryParser.getSchemaService(),
                jooqQueryParser.getSqlCustomizer(),
                jooqQueryParser.getDslContext());

        this.jooqQueryParser = jooqQueryParser;
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
                        parsingCreateTypeMigrationOperation(
                                migration.getDomainTypeName(), (CreateTypeMigrationOperation) operation));
            } else if (operation instanceof CreateFieldMigrationOperation) {
                migrationInSql.addAll(
                        parsingCreateFieldMigrationOperation(
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
                        parsingDropTypeMigrationOperation(
                                migration.getDomainTypeName(), (DropTypeMigrationOperation) operation));
            } else if (operation instanceof DropFieldMigrationOperation) {
                migrationInSql.addAll(
                        parsingDropFieldMigrationOperation(
                                migration.getDomainTypeName(), (DropFieldMigrationOperation) operation));
            } else if (operation instanceof DropUniqueMigrationOperation) {
                migrationInSql.addAll(
                        parsingIndexMigrationOperation(
                                migration.getDomainTypeName(), (DropUniqueMigrationOperation) operation));
            } else if (operation instanceof DropIndexMigrationOperation) {
                migrationInSql.addAll(
                        parsingIndexMigrationOperation(
                                migration.getDomainTypeName(), (DropIndexMigrationOperation) operation));
            } else if (operation instanceof DataMigrationOperation) {
                migrationInSql.addAll(
                        parsingDataMigrationOperation(
                                migration.getDomainTypeName(), (DataMigrationOperation) operation));
            }
        }

        return migrationInSql;
    }

    private List<String> parsingCreateTypeMigrationOperation(
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

    private List<String> parsingCreateFieldMigrationOperation(
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

    private List<String> parsingDropTypeMigrationOperation(
            TypeName typeName, DropTypeMigrationOperation dropTypeMigrationOperation) {
        List<String> operationsInSql = new ArrayList<>();

        String tableName = getSqlCustomizer().getTableName(typeName);

        DomainTypeDefinition domainTypeDefinition = getSchemaService().getDomainTypeDefinition(typeName);

        operationsInSql.addAll(
                parsingIndexMigrationOperation(
                        typeName, new DropUniqueMigrationOperation(domainTypeDefinition.getUniques())));

        operationsInSql.addAll(
                parsingIndexMigrationOperation(
                        typeName, new DropIndexMigrationOperation(domainTypeDefinition.getIndexes())));

        DropTableFinalStep dropTableFinalStep = getDslContext()
                .dropTable(tableName)
                .cascade();

        operationsInSql.add(dropTableFinalStep.getSQL());

        return operationsInSql;
    }

    private List<String> parsingDropFieldMigrationOperation(
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

    private List<String> parsingDataMigrationOperation(
            TypeName domainTypeName, DataMigrationOperation dataMigrationOperation) {
        List<String> operationsInSql = new ArrayList<>();

        DomainTypeDefinition domainTypeDefinition = getSchemaService().getDomainTypeDefinition(domainTypeName);

        for (Map.Entry<String, PublicQuery> queryEntry : dataMigrationOperation.getQueries().entrySet()) {
            if (queryEntry.getValue() instanceof AbstractSaveQuery) {
                operationsInSql.add(
                        jooqQueryParser.getSqlSaveQuery(
                                (AbstractSaveQuery) queryEntry.getValue(), domainTypeDefinition).getSql());
            } else if (queryEntry.getValue() instanceof DeleteQuery) {
                operationsInSql.add(
                        jooqQueryParser.getSqlDeleteQuery((DeleteQuery) queryEntry.getValue()).getSql());
            }
        }

        return operationsInSql;
    }

}
