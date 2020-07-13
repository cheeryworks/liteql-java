package org.cheeryworks.liteql.service;

import org.cheeryworks.liteql.model.type.TypeName;

public class AbstractSqlParser extends AbstractSqlCustomizer implements SqlParser {

    @Override
    public String getTableName(TypeName domainTypeName) {
        return domainTypeName.getFullname().replace(".", "_").toLowerCase();
    }

}
