package org.cheeryworks.liteql.model.util.builder.delete;

import org.cheeryworks.liteql.model.query.delete.DeleteQuery;

public class LiteQLDeleteQueryEndBuilder extends LiteQLDeleteQueryBuilder {

    private LiteQLDeleteQuery liteQLDeleteQuery;

    public LiteQLDeleteQueryEndBuilder(LiteQLDeleteQuery liteQLDeleteQuery) {
        this.liteQLDeleteQuery = liteQLDeleteQuery;
    }

    public DeleteQuery getQuery() {
        return getQuery(this.liteQLDeleteQuery);
    }

    public LiteQLDeleteQuery build() {
        return this.liteQLDeleteQuery;
    }

}
