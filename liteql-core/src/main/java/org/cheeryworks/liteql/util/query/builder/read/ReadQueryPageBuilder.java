package org.cheeryworks.liteql.util.query.builder.read;

import org.cheeryworks.liteql.query.read.PageReadQuery;

public class ReadQueryPageBuilder extends ReadQueryScopeBuilder<PageReadQuery> {

    public ReadQueryPageBuilder(ReadQueryMetadata readQueryMetadata, int page, int size) {
        super(readQueryMetadata, new PageReadQuery());

        readQueryMetadata.setPage(page);
        readQueryMetadata.setSize(size);
    }

}
