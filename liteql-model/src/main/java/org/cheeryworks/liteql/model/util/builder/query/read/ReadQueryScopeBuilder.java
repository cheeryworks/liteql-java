package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.AbstractReadQuery;

public class ReadQueryScopeBuilder<T extends AbstractReadQuery> extends ReadQueryAssociationsBuilder<T> {

    private ReadQueryMetadata readQueryMetadata;

    private Class<T> readQueryType;

    public ReadQueryScopeBuilder(ReadQueryMetadata readQueryMetadata, Class<T> readQueryType) {
        super(readQueryMetadata, readQueryType);

        this.readQueryMetadata = readQueryMetadata;
        this.readQueryType = readQueryType;
    }

    public ReadQueryAssociationsBuilder<T> scope(String scope) {
        this.readQueryMetadata.setScope(scope);

        return new ReadQueryAssociationsBuilder<>(this.readQueryMetadata, readQueryType);
    }

}
