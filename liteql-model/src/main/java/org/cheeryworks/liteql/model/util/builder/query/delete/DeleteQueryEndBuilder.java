package org.cheeryworks.liteql.model.util.builder.query.delete;

import org.cheeryworks.liteql.model.query.delete.DeleteQuery;

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
