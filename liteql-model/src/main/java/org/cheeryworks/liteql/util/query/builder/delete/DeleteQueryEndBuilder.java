package org.cheeryworks.liteql.util.query.builder.delete;

import org.cheeryworks.liteql.query.delete.DeleteQuery;

public class DeleteQueryEndBuilder extends DeleteQueryBuilder {

    private DeleteQueryMetadata deleteQueryMetadata;

    public DeleteQueryEndBuilder(DeleteQueryMetadata deleteQueryMetadata) {
        this.deleteQueryMetadata = deleteQueryMetadata;
    }

    public DeleteQuery getQuery() {
        return getQuery(this.deleteQueryMetadata);
    }

    public DeleteQueryMetadata build() {
        return this.deleteQueryMetadata;
    }

}
