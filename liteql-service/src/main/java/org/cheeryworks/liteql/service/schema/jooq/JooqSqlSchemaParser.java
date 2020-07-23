package org.cheeryworks.liteql.service.schema.jooq;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.schema.DomainType;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.migration.operation.CreateIndexMigrationOperation;
import org.cheeryworks.liteql.schema.migration.operation.CreateUniqueMigrationOperation;
import org.cheeryworks.liteql.service.jooq.AbstractJooqSqlParser;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.schema.SqlSchemaParser;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.jooq.CreateTableColumnStep;
import org.jooq.DSLContext;
import org.jooq.Field;

import java.util.List;
import java.util.Set;

public class JooqSqlSchemaParser extends AbstractJooqSqlParser implements SqlSchemaParser {

    public JooqSqlSchemaParser(
            LiteQLProperties liteQLProperties, SchemaService schemaService,
            DSLContext dslContext, SqlCustomizer sqlCustomizer) {
        super(liteQLProperties, schemaService, dslContext, sqlCustomizer);
    }

    @Override
    public String repositoryToSql() {
        StringBuilder repositorySqlBuilder = new StringBuilder();

        for (String schemaName : getSchemaService().getSchemaNames()) {
            repositorySqlBuilder.append(schemaToSql(schemaName)).append("\n\n");
        }

        return repositorySqlBuilder.toString();
    }

    @Override
    public String schemaToSql(String schemaName) {
        Set<DomainType> domainTypes = getSchemaService().getDomainTypes(schemaName);

        StringBuilder schemaSqlBuilder = new StringBuilder();

        for (DomainType domainType : domainTypes) {
            schemaSqlBuilder.append(domainTypeToSql(domainType));
        }

        return schemaSqlBuilder.toString();
    }

    @Override
    public String domainTypeToSql(TypeName domainTypeName) {
        return domainTypeToSql(getSchemaService().getDomainType(domainTypeName));
    }

    private String domainTypeToSql(DomainType domainType) {
        StringBuilder schemaSqlBuilder = new StringBuilder();

        String tableName = getSqlCustomizer().getTableName(domainType);

        CreateTableColumnStep createTableColumnStep = getDslContext().createTable(tableName);

        for (Field field : getJooqFields(domainType.getFields())) {
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
