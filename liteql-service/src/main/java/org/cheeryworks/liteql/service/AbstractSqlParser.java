package org.cheeryworks.liteql.service;

import org.cheeryworks.liteql.model.type.TypeName;

public abstract class AbstractSqlParser implements SqlParser {

    private SqlCustomizer sqlCustomizer = new DefaultSqlCustomizer();

    public AbstractSqlParser(SqlCustomizer sqlCustomizer) {
        if (sqlCustomizer != null) {
            this.sqlCustomizer = sqlCustomizer;
        }
    }

    protected String getTableName(TypeName domainTypeName) {
        return sqlCustomizer.getTableName(domainTypeName);
    }

}
