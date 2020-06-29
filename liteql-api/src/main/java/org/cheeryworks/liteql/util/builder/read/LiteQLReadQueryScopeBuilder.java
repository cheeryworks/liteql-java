package org.cheeryworks.liteql.util.builder.read;

import org.cheeryworks.liteql.model.query.read.AbstractReadQuery;

public class LiteQLReadQueryScopeBuilder<T extends AbstractReadQuery> extends LiteQLReadQueryAssociationsBuilder<T> {

    private LiteQLReadQuery liteQLReadQuery;

    private Class<T> readQueryType;

    public LiteQLReadQueryScopeBuilder(LiteQLReadQuery liteQLReadQuery, Class<T> readQueryType) {
        super(liteQLReadQuery, readQueryType);

        this.liteQLReadQuery = liteQLReadQuery;
        this.readQueryType = readQueryType;
    }

    public LiteQLReadQueryAssociationsBuilder<T> scope(String scope) {
        this.liteQLReadQuery.setScope(scope);

        return new LiteQLReadQueryAssociationsBuilder<>(this.liteQLReadQuery, readQueryType);
    }

}
