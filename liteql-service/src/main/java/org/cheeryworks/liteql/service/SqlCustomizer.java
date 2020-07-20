package org.cheeryworks.liteql.service;

import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.model.type.TypeName;

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

    default String getFieldName(TypeName domainTypeName, String columnName) {
        String fieldName = columnName.toLowerCase();

        String[] wordsOfColumnName = fieldName.split("_");

        StringBuffer fieldNameBuffer = new StringBuffer();

        for (int i = 0; i < wordsOfColumnName.length; i++) {
            fieldNameBuffer.append((i == 0) ? wordsOfColumnName[i] : StringUtils.capitalize(wordsOfColumnName[i]));
        }

        return fieldNameBuffer.toString();
    }

}
