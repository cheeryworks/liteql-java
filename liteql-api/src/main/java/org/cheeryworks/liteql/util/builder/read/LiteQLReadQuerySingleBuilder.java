package org.cheeryworks.liteql.util.builder.read;

import org.cheeryworks.liteql.model.query.read.SingleReadQuery;

public class LiteQLReadQuerySingleBuilder extends LiteQLReadQueryScopeBuilder<SingleReadQuery> {

    public LiteQLReadQuerySingleBuilder(LiteQLReadQuery liteQLReadQuery) {
        super(liteQLReadQuery, SingleReadQuery.class);
    }

}
