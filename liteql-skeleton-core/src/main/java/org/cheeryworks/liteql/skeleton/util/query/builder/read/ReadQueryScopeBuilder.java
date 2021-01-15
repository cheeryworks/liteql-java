package org.cheeryworks.liteql.skeleton.util.query.builder.read;

import org.cheeryworks.liteql.skeleton.query.read.AbstractTypedReadQuery;

public class ReadQueryScopeBuilder<T extends AbstractTypedReadQuery> extends ReadQueryAssociationsBuilder<T> {

    public ReadQueryScopeBuilder(ReadQueryMetadata readQueryMetadata, T readQuery) {
        super(readQueryMetadata, readQuery);
    }

    public ReadQueryAssociationsBuilder<T> scope(String scope) {
        getReadQueryMetadata().setScope(scope);

        return this;
    }

}
