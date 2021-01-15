package org.cheeryworks.liteql.skeleton.service.sql;

import org.cheeryworks.liteql.skeleton.schema.TypeName;

public interface SqlCustomizer {

    default String getTableName(TypeName domainTypeName) {
        return domainTypeName.getFullname().replace(".", "_").toLowerCase();
    }

    default String getColumnName(TypeName domainTypeName, String fieldName) {
        return fieldName.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                "_"
        ).toLowerCase();
    }

}
