package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.AbstractTypedReadQuery;

public class ReadQueryScopeBuilder<T extends AbstractTypedReadQuery> extends ReadQueryAssociationsBuilder<T> {

    public ReadQueryScopeBuilder(ReadQueryMetadata readQueryMetadata, T readQuery) {
        super(readQueryMetadata, readQuery);
    }

    public ReadQueryAssociationsBuilder<T> scope(String scope) {
        getReadQueryMetadata().setScope(scope);

        return this;
    }

}
