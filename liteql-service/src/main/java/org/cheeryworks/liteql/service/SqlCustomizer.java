package org.cheeryworks.liteql.service;

import org.cheeryworks.liteql.model.type.TypeName;

public interface SqlCustomizer {

    String getTableName(TypeName domainTypeName);

}
