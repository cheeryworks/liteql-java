package org.cheeryworks.liteql.service.query.sql;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.service.sql.AbstractSqlService;
import org.cheeryworks.liteql.service.sql.SqlCustomizer;
import org.cheeryworks.liteql.service.sql.SqlExecutor;

public abstract class AbstractSqlExecutor extends AbstractSqlService implements SqlExecutor {

    public AbstractSqlExecutor(LiteQLProperties liteQLProperties, SqlCustomizer sqlCustomizer) {
        super(liteQLProperties, sqlCustomizer);
    }

}