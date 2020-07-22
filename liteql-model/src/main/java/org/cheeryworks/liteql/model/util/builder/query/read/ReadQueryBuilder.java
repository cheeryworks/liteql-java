package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.ReadQuery;

public class ReadQueryBuilder extends ReadQueryScopeBuilder<ReadQuery> {

    public ReadQueryBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata, new ReadQuery());
    }

    public ReadQuerySingleBuilder single() {
        return new ReadQuerySingleBuilder(getReadQueryMetadata());
    }

    public ReadQueryPageBuilder page(int page, int size) {
        return new ReadQueryPageBuilder(getReadQueryMetadata(), page, size);
    }

    public ReadQueryTreeBuilder tree() {
        return new ReadQueryTreeBuilder(getReadQueryMetadata());
    }

}
