package org.cheeryworks.liteql.service;

import org.cheeryworks.liteql.LiteQLProperties;

public class AbstractLiteQLService implements LiteQLService {

    private LiteQLProperties liteQLProperties = new LiteQLProperties();

    public AbstractLiteQLService(LiteQLProperties liteQLProperties) {
        if (liteQLProperties != null) {
            this.liteQLProperties = liteQLProperties;
        }
    }

    @Override
    public LiteQLProperties getLiteQLProperties() {
        return this.liteQLProperties;
    }

}
