package org.cheeryworks.liteql.util.query.builder.read;

import org.cheeryworks.liteql.query.read.TreeReadQuery;

public class ReadQueryTreeBuilder extends ReadQueryScopeBuilder<TreeReadQuery> {

    public ReadQueryTreeBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata, new TreeReadQuery());
    }

}
