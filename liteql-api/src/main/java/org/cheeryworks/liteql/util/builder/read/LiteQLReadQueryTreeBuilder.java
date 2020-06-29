package org.cheeryworks.liteql.util.builder.read;

import org.cheeryworks.liteql.model.query.read.TreeReadQuery;

public class LiteQLReadQueryTreeBuilder extends LiteQLReadQueryScopeBuilder<TreeReadQuery> {

    public LiteQLReadQueryTreeBuilder(LiteQLReadQuery liteQLReadQuery, Integer expandLevel) {
        super(liteQLReadQuery, TreeReadQuery.class);

        liteQLReadQuery.setExpandLevel(expandLevel);
    }

}
