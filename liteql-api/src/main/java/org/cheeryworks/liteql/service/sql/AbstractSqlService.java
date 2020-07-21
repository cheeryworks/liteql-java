package org.cheeryworks.liteql.service.sql;

public abstract class AbstractSqlService implements SqlParser {

    private SqlCustomizer sqlCustomizer = new DefaultSqlCustomizer();

    public AbstractSqlService(SqlCustomizer sqlCustomizer) {
        if (sqlCustomizer != null) {
            this.sqlCustomizer = sqlCustomizer;
        }
    }

    @Override
    public SqlCustomizer getSqlCustomizer() {
        return this.sqlCustomizer;
    }

}
