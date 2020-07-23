package org.cheeryworks.liteql.util.query.builder.read;

import org.cheeryworks.liteql.query.read.SingleReadQuery;

public class ReadQuerySingleBuilder extends ReadQueryScopeBuilder<SingleReadQuery> {

    public ReadQuerySingleBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata, new SingleReadQuery());
    }

}
