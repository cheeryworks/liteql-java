package org.cheeryworks.liteql.service.query.sql;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.service.sql.AbstractSqlService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.service.sql.SqlParser;

public abstract class AbstractSqlParser extends AbstractSqlService implements SqlParser {

    public AbstractSqlParser(LiteQLProperties liteQLProperties, SqlCustomizer sqlCustomizer) {
        super(liteQLProperties, sqlCustomizer);
    }

}