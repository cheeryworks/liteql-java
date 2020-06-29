package org.cheeryworks.liteql.util.builder.read;

import org.cheeryworks.liteql.model.query.read.ReadQuery;

public class LiteQLReadQueryTypeBuilder extends LiteQLReadQueryAssociationsBuilder<ReadQuery> {

    private LiteQLReadQuery liteQLReadQuery;

    public LiteQLReadQueryTypeBuilder(LiteQLReadQuery liteQLReadQuery) {
        super(liteQLReadQuery, ReadQuery.class);

        this.liteQLReadQuery = liteQLReadQuery;
    }

    public LiteQLReadQuerySingleBuilder single() {
        return new LiteQLReadQuerySingleBuilder(this.liteQLReadQuery);
    }

    public LiteQLReadQueryPageBuilder page(int page, int size) {
        return new LiteQLReadQueryPageBuilder(this.liteQLReadQuery, page, size);
    }

    public LiteQLReadQueryTreeBuilder tree() {
        return new LiteQLReadQueryTreeBuilder(this.liteQLReadQuery, null);
    }

    public LiteQLReadQueryTreeBuilder tree(Integer expandLevel) {
        return new LiteQLReadQueryTreeBuilder(this.liteQLReadQuery, expandLevel);
    }

    public LiteQLReadQueryAssociationsBuilder<ReadQuery> scope(String scope) {
        this.liteQLReadQuery.setScope(scope);

        return new LiteQLReadQueryAssociationsBuilder<>(this.liteQLReadQuery, ReadQuery.class);
    }

}
