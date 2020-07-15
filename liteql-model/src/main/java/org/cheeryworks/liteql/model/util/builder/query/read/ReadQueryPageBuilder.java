package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.PageReadQuery;

public class ReadQueryPageBuilder extends ReadQueryScopeBuilder<PageReadQuery> {

    public ReadQueryPageBuilder(ReadQueryMetadata liteQLReadQuery, int page, int size) {
        super(liteQLReadQuery, PageReadQuery.class);

        liteQLReadQuery.setPage(page);
        liteQLReadQuery.setSize(size);
    }

}
