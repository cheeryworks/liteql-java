package org.cheeryworks.liteql.service.type;

import org.cheeryworks.liteql.model.type.DomainTypeName;

public interface SqlSchemaParser {

    String PRIMARY_KEY_PREFIX = "pk_";

    String UNIQUE_KEY_PREFIX = "uk_";

    String INDEX_KEY_PREFIX = "idx_";

    String repositoryToSql();

    String schemaToSql(String schemaName);

    String domainTypeToSql(DomainTypeName domainTypeName);

}
