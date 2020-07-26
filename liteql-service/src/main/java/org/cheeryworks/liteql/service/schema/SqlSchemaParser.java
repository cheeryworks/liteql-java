package org.cheeryworks.liteql.service.schema;

import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.service.sql.SqlParser;

public interface SqlSchemaParser extends SqlParser {

    String schemaToSql();

    String schemaToSql(String schemaName);

    String domainTypeToSql(TypeName domainTypeName);

}
