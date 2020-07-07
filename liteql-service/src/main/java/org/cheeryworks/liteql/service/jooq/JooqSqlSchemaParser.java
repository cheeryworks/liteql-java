package org.cheeryworks.liteql.service.jooq;

import org.cheeryworks.liteql.model.type.DomainType;
import org.cheeryworks.liteql.model.type.Type;
import org.cheeryworks.liteql.model.type.migration.operation.CreateIndexMigrationOperation;
import org.cheeryworks.liteql.model.type.migration.operation.CreateUniqueMigrationOperation;
import org.cheeryworks.liteql.service.enums.Database;
import org.cheeryworks.liteql.service.repository.Repository;
import org.cheeryworks.liteql.service.type.SqlSchemaParser;
import org.jooq.CreateTableColumnStep;
import org.jooq.Field;

import java.util.List;
import java.util.Set;

public class JooqSqlSchemaParser extends AbstractJooqSqlParser implements SqlSchemaParser {

    public JooqSqlSchemaParser(Repository repository, Database database) {
        super(repository, database);
    }

    @Override
    public String repositoryToSql() {
        StringBuilder repositorySqlBuilder = new StringBuilder();

        for (String schemaName : getRepository().getSchemaNames()) {
            repositorySqlBuilder.append(schemaToSql(schemaName)).append("\n\n");
        }

        return repositorySqlBuilder.toString();
    }

    @Override
    public String schemaToSql(String schemaName) {
        Set<DomainType> domainTypes = getRepository().getDomainTypes(schemaName);

        StringBuilder schemaSqlBuilder = new StringBuilder();

        for (DomainType domainType : domainTypes) {
            schemaSqlBuilder.append(domainTypeToSql(domainType));
        }

        return schemaSqlBuilder.toString();
    }

    @Override
    public String domainTypeToSql(Type domainType) {
        return domainTypeToSql(getRepository().getDomainType(domainType));
    }

    private String domainTypeToSql(DomainType domainType) {
        StringBuilder schemaSqlBuilder = new StringBuilder();

        String tableName = getTableName(domainType.getFullname());

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
                domainType, new CreateUniqueMigrationOperation(domainType.getUniques()));

        for (String uniqueSql : uniqueSqls) {
            schemaSqlBuilder.append(uniqueSql).append(";").append("\n\n");
        }

        List<String> indexSqls = parsingIndexMigrationOperation(
                domainType, new CreateIndexMigrationOperation(domainType.getIndexes()));

        for (String indexSql : indexSqls) {
            schemaSqlBuilder.append(indexSql).append(";").append("\n\n");
        }

        return schemaSqlBuilder.toString();
    }


}
