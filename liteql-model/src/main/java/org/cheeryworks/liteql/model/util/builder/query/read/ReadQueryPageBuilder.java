package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.PageReadQuery;

public class ReadQueryPageBuilder extends ReadQueryScopeBuilder<PageReadQuery> {

    public ReadQueryPageBuilder(ReadQueryMetadata readQueryMetadata, int page, int size) {
        super(readQueryMetadata, new PageReadQuery());

        readQueryMetadata.setPage(page);
        readQueryMetadata.setSize(size);
    }

}
