package org.cheeryworks.liteql.service;

import org.cheeryworks.liteql.LiteQLProperties;

public class AbstractLiteQLService implements LiteQLService {

    private LiteQLProperties liteQLProperties;

    public AbstractLiteQLService(LiteQLProperties liteQLProperties) {
        this.liteQLProperties = liteQLProperties;
    }

    @Override
    public LiteQLProperties getLiteQLProperties() {
        return this.liteQLProperties;
    }

}
