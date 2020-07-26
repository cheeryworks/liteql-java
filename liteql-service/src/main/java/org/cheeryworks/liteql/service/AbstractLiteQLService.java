package org.cheeryworks.liteql.service;

import org.cheeryworks.liteql.LiteQLProperties;

public class AbstractLiteQLService {

    private LiteQLProperties liteQLProperties;

    public AbstractLiteQLService(LiteQLProperties liteQLProperties) {
        this.liteQLProperties = liteQLProperties;
    }

    public LiteQLProperties getLiteQLProperties() {
        return this.liteQLProperties;
    }

}
