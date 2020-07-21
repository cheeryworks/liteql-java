package org.cheeryworks.liteql.service.schema;

import org.cheeryworks.liteql.model.type.TypeName;
import org.cheeryworks.liteql.service.sql.SqlParser;

public interface SqlSchemaParser extends SqlParser {

    String PRIMARY_KEY_PREFIX = "pk_";

    String UNIQUE_KEY_PREFIX = "uk_";

    String INDEX_KEY_PREFIX = "idx_";

    String repositoryToSql();

    String schemaToSql(String schemaName);

    String domainTypeToSql(TypeName domainTypeName);

}
