package org.cheeryworks.liteql.skeleton.service.query.sql;

import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.service.sql.AbstractSqlService;
import org.cheeryworks.liteql.skeleton.service.sql.SqlExecutor;

public abstract class AbstractSqlExecutor extends AbstractSqlService implements SqlExecutor {

    public AbstractSqlExecutor(LiteQLProperties liteQLProperties) {
        super(liteQLProperties);
    }

}
