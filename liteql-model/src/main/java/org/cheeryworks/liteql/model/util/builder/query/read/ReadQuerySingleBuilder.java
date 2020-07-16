package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.SingleReadQuery;

public class ReadQuerySingleBuilder extends ReadQueryScopeBuilder<SingleReadQuery> {

    public ReadQuerySingleBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata, new SingleReadQuery());
    }

}
