package org.cheeryworks.liteql.util.query.builder.save;

import org.cheeryworks.liteql.query.save.AbstractSaveQuery;

public class SaveQueryEndBuilder<T extends AbstractSaveQuery> extends SaveQueryBuilder<T> {

    private SaveQueryMetadata<T> saveQueryMetadata;

    public SaveQueryEndBuilder(SaveQueryMetadata<T> saveQueryMetadata) {
        this.saveQueryMetadata = saveQueryMetadata;
    }

    public T getQuery() {
        return getQuery(this.saveQueryMetadata);
    }

    public SaveQueryMetadata<T> build() {
        return this.saveQueryMetadata;
    }

}
