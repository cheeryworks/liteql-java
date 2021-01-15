package org.cheeryworks.liteql.skeleton.service.sql;

import org.cheeryworks.liteql.skeleton.LiteQLProperties;
import org.cheeryworks.liteql.skeleton.service.AbstractLiteQLService;

public abstract class AbstractSqlService extends AbstractLiteQLService implements SqlService {

    public AbstractSqlService(LiteQLProperties liteQLProperties) {
        super(liteQLProperties);
    }

}
