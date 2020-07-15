package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.AbstractReadQuery;

public class ReadQueryScopeBuilder<T extends AbstractReadQuery> extends ReadQueryAssociationsBuilder<T> {

    private ReadQueryMetadata liteQLReadQuery;

    private Class<T> readQueryType;

    public ReadQueryScopeBuilder(ReadQueryMetadata liteQLReadQuery, Class<T> readQueryType) {
        super(liteQLReadQuery, readQueryType);

        this.liteQLReadQuery = liteQLReadQuery;
        this.readQueryType = readQueryType;
    }

    public ReadQueryAssociationsBuilder<T> scope(String scope) {
        this.liteQLReadQuery.setScope(scope);

        return new ReadQueryAssociationsBuilder<>(this.liteQLReadQuery, readQueryType);
    }

}
