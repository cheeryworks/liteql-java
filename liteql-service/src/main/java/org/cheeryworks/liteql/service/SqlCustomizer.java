package org.cheeryworks.liteql.service;

import org.cheeryworks.liteql.model.type.TypeName;

public interface SqlCustomizer {

    default String getTableName(TypeName domainTypeName) {
        return domainTypeName.getFullname().replace(".", "_").toLowerCase();
    }

}
