package org.cheeryworks.liteql.skeleton.service.schema;

import org.cheeryworks.liteql.skeleton.schema.TypeName;
import org.cheeryworks.liteql.skeleton.service.sql.SqlParser;

public interface SqlSchemaParser extends SqlParser {

    String schemaToSql();

    String schemaToSql(String schemaName);

    String domainTypeToSql(TypeName domainTypeName);

}
