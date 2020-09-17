package org.cheeryworks.liteql.service.sql;

import org.cheeryworks.liteql.service.schema.SchemaService;

public interface SqlParser {

    SchemaService getSchemaService();

    SqlCustomizer getSqlCustomizer();

}
