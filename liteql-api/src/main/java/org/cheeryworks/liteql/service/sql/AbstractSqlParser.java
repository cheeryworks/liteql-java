package org.cheeryworks.liteql.service.sql;

public abstract class AbstractSqlParser extends AbstractSqlService implements SqlParser {

    public AbstractSqlParser(SqlCustomizer sqlCustomizer) {
        super(sqlCustomizer);
    }

}
