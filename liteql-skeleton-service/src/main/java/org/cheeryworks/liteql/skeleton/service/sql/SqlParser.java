package org.cheeryworks.liteql.skeleton.service.sql;

import org.cheeryworks.liteql.skeleton.service.schema.SchemaService;

public interface SqlParser {

    SchemaService getSchemaService();

    SqlCustomizer getSqlCustomizer();

}
