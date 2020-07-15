package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.ReadQuery;

public class ReadQueryTypeBuilder extends ReadQueryAssociationsBuilder<ReadQuery> {

    private ReadQueryMetadata liteQLReadQuery;

    public ReadQueryTypeBuilder(ReadQueryMetadata liteQLReadQuery) {
        super(liteQLReadQuery, ReadQuery.class);

        this.liteQLReadQuery = liteQLReadQuery;
    }

    public ReadQuerySingleBuilder single() {
        return new ReadQuerySingleBuilder(this.liteQLReadQuery);
    }

    public ReadQueryPageBuilder page(int page, int size) {
        return new ReadQueryPageBuilder(this.liteQLReadQuery, page, size);
    }

    public ReadQueryTreeBuilder tree() {
        return new ReadQueryTreeBuilder(this.liteQLReadQuery, null);
    }

    public ReadQueryTreeBuilder tree(Integer expandLevel) {
        return new ReadQueryTreeBuilder(this.liteQLReadQuery, expandLevel);
    }

    public ReadQueryAssociationsBuilder<ReadQuery> scope(String scope) {
        this.liteQLReadQuery.setScope(scope);

        return new ReadQueryAssociationsBuilder<>(this.liteQLReadQuery, ReadQuery.class);
    }

}
