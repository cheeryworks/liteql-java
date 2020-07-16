package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.TreeReadQuery;

public class ReadQueryTreeBuilder extends ReadQueryScopeBuilder<TreeReadQuery> {

    public ReadQueryTreeBuilder(ReadQueryMetadata readQueryMetadata, Integer expandLevel) {
        super(readQueryMetadata, new TreeReadQuery());

        readQueryMetadata.setExpandLevel(expandLevel);
    }

}
