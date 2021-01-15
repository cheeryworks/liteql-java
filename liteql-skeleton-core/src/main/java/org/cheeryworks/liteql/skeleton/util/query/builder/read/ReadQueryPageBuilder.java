package org.cheeryworks.liteql.skeleton.util.query.builder.read;

import org.cheeryworks.liteql.skeleton.query.read.PageReadQuery;

public class ReadQueryPageBuilder extends ReadQueryScopeBuilder<PageReadQuery> {

    public ReadQueryPageBuilder(ReadQueryMetadata readQueryMetadata, int page, int size) {
        super(readQueryMetadata, new PageReadQuery());

        readQueryMetadata.setPage(page);
        readQueryMetadata.setSize(size);
    }

}
