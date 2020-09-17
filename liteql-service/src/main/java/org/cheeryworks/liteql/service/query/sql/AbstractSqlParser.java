package org.cheeryworks.liteql.service.query.sql;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.service.schema.SchemaService;
import org.cheeryworks.liteql.service.sql.AbstractSqlService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.service.sql.SqlParser;

public abstract class AbstractSqlParser extends AbstractSqlService implements SqlParser {

    private SchemaService schemaService;

    private SqlCustomizer sqlCustomizer;

    public AbstractSqlParser(
            LiteQLProperties liteQLProperties, SchemaService schemaService, SqlCustomizer sqlCustomizer) {
        super(liteQLProperties);

        this.schemaService = schemaService;
        this.sqlCustomizer = sqlCustomizer;
    }

    @Override
    public SchemaService getSchemaService() {
        return this.schemaService;
    }

    @Override
    public SqlCustomizer getSqlCustomizer() {
        return this.sqlCustomizer;
    }

}
