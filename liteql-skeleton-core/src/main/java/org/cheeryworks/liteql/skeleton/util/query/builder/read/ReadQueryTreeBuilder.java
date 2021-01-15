package org.cheeryworks.liteql.skeleton.util.query.builder.read;

import org.cheeryworks.liteql.skeleton.query.read.TreeReadQuery;

public class ReadQueryTreeBuilder extends ReadQueryScopeBuilder<TreeReadQuery> {

    public ReadQueryTreeBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata, new TreeReadQuery());
    }

}
