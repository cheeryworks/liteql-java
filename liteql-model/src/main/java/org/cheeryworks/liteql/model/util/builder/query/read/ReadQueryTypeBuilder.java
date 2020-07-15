package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.ReadQuery;

public class ReadQueryTypeBuilder extends ReadQueryAssociationsBuilder<ReadQuery> {

    private ReadQueryMetadata readQueryMetadata;

    public ReadQueryTypeBuilder(ReadQueryMetadata readQueryMetadata) {
        super(readQueryMetadata, ReadQuery.class);

        this.readQueryMetadata = readQueryMetadata;
    }

    public ReadQuerySingleBuilder single() {
        return new ReadQuerySingleBuilder(this.readQueryMetadata);
    }

    public ReadQueryPageBuilder page(int page, int size) {
        return new ReadQueryPageBuilder(this.readQueryMetadata, page, size);
    }

    public ReadQueryTreeBuilder tree() {
        return new ReadQueryTreeBuilder(this.readQueryMetadata, null);
    }

    public ReadQueryTreeBuilder tree(Integer expandLevel) {
        return new ReadQueryTreeBuilder(this.readQueryMetadata, expandLevel);
    }

    public ReadQueryAssociationsBuilder<ReadQuery> scope(String scope) {
        this.readQueryMetadata.setScope(scope);

        return new ReadQueryAssociationsBuilder<>(this.readQueryMetadata, ReadQuery.class);
    }

}
