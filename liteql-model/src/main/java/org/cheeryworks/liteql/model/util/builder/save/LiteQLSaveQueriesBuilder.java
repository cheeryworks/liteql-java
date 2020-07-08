package org.cheeryworks.liteql.model.util.builder.save;

import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;

import java.util.ArrayList;
import java.util.List;

public class LiteQLSaveQueriesBuilder<T extends AbstractSaveQuery> extends LiteQLSaveQueryBuilder<T> {

    private LiteQLSaveQuery<T>[] liteQLSaveQueries;

    public LiteQLSaveQueriesBuilder(LiteQLSaveQuery<T>... liteQLSaveQueries) {
        this.liteQLSaveQueries = liteQLSaveQueries;
    }

    public List<T> getQueries() {
        List<T> saveQueries = new ArrayList<>();

        for (LiteQLSaveQuery<T> liteQLSaveQuery : liteQLSaveQueries) {
            saveQueries.add(getQuery(liteQLSaveQuery));
        }

        return saveQueries;
    }

}
