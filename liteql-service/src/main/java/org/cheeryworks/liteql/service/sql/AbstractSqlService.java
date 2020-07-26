package org.cheeryworks.liteql.service.sql;

import org.cheeryworks.liteql.LiteQLProperties;
import org.cheeryworks.liteql.service.AbstractLiteQLService;

public abstract class AbstractSqlService extends AbstractLiteQLService implements SqlService {

    public AbstractSqlService(LiteQLProperties liteQLProperties) {
        super(liteQLProperties);
    }

}
