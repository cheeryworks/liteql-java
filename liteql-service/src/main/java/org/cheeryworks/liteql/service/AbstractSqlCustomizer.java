package org.cheeryworks.liteql.service;

import org.cheeryworks.liteql.model.type.TypeName;

public class AbstractSqlCustomizer implements SqlCustomizer {

    @Override
    public String getTableName(TypeName domainTypeName) {
        return domainTypeName.getFullname().replace(".", "_").toLowerCase();
    }

}
