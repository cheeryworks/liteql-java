package org.cheeryworks.liteql.util.builder.save;

import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;

public class LiteQLSaveQueryEndBuilder<T extends AbstractSaveQuery> extends LiteQLSaveQueryBuilder<T> {

    private LiteQLSaveQuery<T> liteQLSaveQuery;

    public LiteQLSaveQueryEndBuilder(LiteQLSaveQuery<T> liteQLSaveQuery) {
        this.liteQLSaveQuery = liteQLSaveQuery;
    }

    public T getQuery() {
        return getQuery(this.liteQLSaveQuery);
    }

    public LiteQLSaveQuery<T> build() {
        return this.liteQLSaveQuery;
    }

}
