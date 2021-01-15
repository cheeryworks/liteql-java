package org.cheeryworks.liteql.skeleton.service;

import org.cheeryworks.liteql.skeleton.LiteQLProperties;

public class AbstractLiteQLService {

    private LiteQLProperties liteQLProperties;

    public AbstractLiteQLService(LiteQLProperties liteQLProperties) {
        this.liteQLProperties = liteQLProperties;
    }

    public LiteQLProperties getLiteQLProperties() {
        return this.liteQLProperties;
    }

}
