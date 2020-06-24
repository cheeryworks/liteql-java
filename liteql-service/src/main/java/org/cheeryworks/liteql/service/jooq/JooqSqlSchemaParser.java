package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.migration.operation.CreateIndexMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateUniqueMigrationOperation;
import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.service.enums.Database;
import org.cheeryworks.liteql.service.type.SqlSchemaParser;
import org.jooq.CreateTableColumnStep;
import org.jooq.Field;

import java.util.List;
import java.util.Map;

public class JooqSqlSchemaParser extends AbstractJooqSqlParser implements SqlSchemaParser {

    public JooqSqlSchemaParser(Repository repository, Database database) {
        super(repository, database);
    }

    @Override
    public String repositoryToSql() {
        StringBuilder repositorySqlBuilder = new StringBuilder();

        for (String schemaName : getRepository().getSchemas()) {
            repositorySqlBuilder.append(schemaToSql(schemaName)).append("\n\n");
        }

        return repositorySqlBuilder.toString();
    }

    @Override
    public String schemaToSql(String schemaName) {
        Map<String, DomainType> domainTypes = getRepository().getDomainTypes(schemaName);

        StringBuilder schemaSqlBuilder = new StringBuilder();

        for (DomainType domainType : domainTypes.values()) {
            schemaSqlBuilder.append(domainTypeToSql(domainType));
        }

        return schemaSqlBuilder.toString();
    }

    @Override
    public String domainTypeToSql(String domainTypeName) {
        return domainTypeToSql(getRepository().getDomainType(domainTypeName));
    }

    private String domainTypeToSql(DomainType domainType) {
        StringBuilder schemaSqlBuilder = new StringBuilder();

        String tableName = getTableName(domainType.getName());

        if (tableName.length() > 25) {
            throw new IllegalArgumentException("Schema or Domain Type name is too long, "
                    + "max length of table name[schemaName + '_' + domainTypeName] is 25 chars");
        }

        CreateTableColumnStep createTableColumnStep = getDslContext().createTable(tableName);

        for (Field field : getJooqFields(domainType.getFields(), getDatabase())) {
            if (createTableColumnStep == null) {
                createTableColumnStep = createTableColumnStep.column(field, field.getDataType());
            } else {
                createTableColumnStep.column(field, field.getDataType());
            }
        }

        schemaSqlBuilder.append(createTableColumnStep.getSQL()).append(";").append("\n\n");

        schemaSqlBuilder.append(parsingAddPrimaryKey(tableName)).append(";").append("\n\n");

        List<String> uniqueSqls = parsingIndexMigrationOperation(
                tableName, new CreateUniqueMigrationOperation(domainType.getUniques()));

        for (String uniqueSql : uniqueSqls) {
            schemaSqlBuilder.append(uniqueSql).append(";").append("\n\n");
        }

        List<String> indexSqls = parsingIndexMigrationOperation(
                tableName, new CreateIndexMigrationOperation(domainType.getIndexes()));

        for (String indexSql : indexSqls) {
            schemaSqlBuilder.append(indexSql).append(";").append("\n\n");
        }

        return schemaSqlBuilder.toString();
    }


}
