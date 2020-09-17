package org.cheeryworks.liteql.service.schema.jooq;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.schema.DomainTypeDefinition;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.schema.migration.operation.CreateIndexMigrationOperation;
import org.cheeryworks.liteql.schema.migration.operation.CreateUniqueMigrationOperation;
import org.cheeryworks.liteql.service.jooq.AbstractJooqParser;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.schema.SqlSchemaParser;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.jooq.CreateTableColumnStep;
import org.jooq.DSLContext;
import org.jooq.Field;

import java.util.List;
import java.util.Set;

public class JooqSchemaParser extends AbstractJooqParser implements SqlSchemaParser {

    public JooqSchemaParser(
            LiteQLProperties liteQLProperties,
            SchemaService schemaService, SqlCustomizer sqlCustomizer,
            DSLContext dslContext) {
        super(liteQLProperties, schemaService, sqlCustomizer, dslContext);
    }

    @Override
    public String schemaToSql() {
        StringBuilder repositorySqlBuilder = new StringBuilder();

        for (String schemaName : getSchemaService().getSchemaNames()) {
            repositorySqlBuilder.append(schemaToSql(schemaName)).append("\n\n");
        }

        return repositorySqlBuilder.toString();
    }

    @Override
    public String schemaToSql(String schemaName) {
        Set<DomainTypeDefinition> domainTypeDefinitions = getSchemaService().getDomainTypeDefinitions(schemaName);

        StringBuilder schemaSqlBuilder = new StringBuilder();

        for (DomainTypeDefinition domainTypeDefinition : domainTypeDefinitions) {
            schemaSqlBuilder.append(domainTypeDefinitionToSql(domainTypeDefinition));
        }

        return schemaSqlBuilder.toString();
    }

    @Override
    public String domainTypeToSql(TypeName domainTypeName) {
        return domainTypeDefinitionToSql(getSchemaService().getDomainTypeDefinition(domainTypeName));
    }

    private String domainTypeDefinitionToSql(DomainTypeDefinition domainTypeDefinition) {
        StringBuilder schemaSqlBuilder = new StringBuilder();

        String tableName = getSqlCustomizer().getTableName(domainTypeDefinition.getTypeName());

        CreateTableColumnStep createTableColumnStep = getDslContext().createTable(tableName);

        for (Field field : getJooqFields(domainTypeDefinition.getFields())) {
            if (createTableColumnStep == null) {
                createTableColumnStep = createTableColumnStep.column(field, field.getDataType());
            } else {
                createTableColumnStep.column(field, field.getDataType());
            }
        }

        schemaSqlBuilder.append(createTableColumnStep.getSQL()).append(";").append("\n\n");

        schemaSqlBuilder.append(parsingAddPrimaryKey(tableName)).append(";").append("\n\n");

        List<String> uniqueSqls = parsingIndexMigrationOperation(
                domainTypeDefinition.getTypeName(),
                new CreateUniqueMigrationOperation(domainTypeDefinition.getUniques()));

        for (String uniqueSql : uniqueSqls) {
            schemaSqlBuilder.append(uniqueSql).append(";").append("\n\n");
        }

        List<String> indexSqls = parsingIndexMigrationOperation(
                domainTypeDefinition.getTypeName(),
                new CreateIndexMigrationOperation(domainTypeDefinition.getIndexes()));

        for (String indexSql : indexSqls) {
            schemaSqlBuilder.append(indexSql).append(";").append("\n\n");
        }

        return schemaSqlBuilder.toString();
    }

}
