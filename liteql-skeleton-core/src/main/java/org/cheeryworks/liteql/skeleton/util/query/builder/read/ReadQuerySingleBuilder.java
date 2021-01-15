package org.cheeryworks.liteql.skeleton.util.query.builder.read;

import org.cheeryworks.liteql.skeleton.query.read.SingleReadQuery;

public class ReadQuerySingleBuilder extends ReadQueryScopeBuilder<SingleReadQuery> {

    public ReadQuerySingleBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata, new SingleReadQuery());
    }

}
