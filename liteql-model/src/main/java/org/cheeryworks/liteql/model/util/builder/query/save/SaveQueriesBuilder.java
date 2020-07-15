package org.cheeryworks.liteql.model.util.builder.query.save;

import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;

import java.util.ArrayList;
import java.util.List;

public class SaveQueriesBuilder<T extends AbstractSaveQuery> extends SaveQueryBuilder<T> {

    private SaveQueryMetadata<T>[] liteQLSaveQueries;

    public SaveQueriesBuilder(SaveQueryMetadata<T>... liteQLSaveQueries) {
        this.liteQLSaveQueries = liteQLSaveQueries;
    }

    public List<T> getQueries() {
        List<T> saveQueries = new ArrayList<>();

        for (SaveQueryMetadata<T> saveQueryMetadata : liteQLSaveQueries) {
            saveQueries.add(getQuery(saveQueryMetadata));
        }

        return saveQueries;
    }

}
