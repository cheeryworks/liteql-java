package org.cheeryworks.liteql.util.builder.read;

import org.cheeryworks.liteql.model.query.read.PageReadQuery;

public class LiteQLReadQueryPageBuilder extends LiteQLReadQueryScopeBuilder<PageReadQuery> {

    public LiteQLReadQueryPageBuilder(LiteQLReadQuery liteQLReadQuery, int page, int size) {
        super(liteQLReadQuery, PageReadQuery.class);

        liteQLReadQuery.setPage(page);
        liteQLReadQuery.setSize(size);
    }

}
