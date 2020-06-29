package org.cheeryworks.liteql.util.builder.delete;

import org.cheeryworks.liteql.model.query.delete.DeleteQuery;

import java.util.LinkedList;
import java.util.List;

public class LiteQLDeleteQueriesBuilder extends LiteQLDeleteQueryBuilder {

    private LiteQLDeleteQuery[] liteQLDeleteQueries;

    public LiteQLDeleteQueriesBuilder(LiteQLDeleteQuery... liteQLDeleteQueries) {
        this.liteQLDeleteQueries = liteQLDeleteQueries;
    }

    public List<DeleteQuery> getQueries() {
        List<DeleteQuery> deleteQueries = new LinkedList<>();

        for (LiteQLDeleteQuery liteQLDeleteQuery : liteQLDeleteQueries) {
            deleteQueries.add(getQuery(liteQLDeleteQuery));
        }

        return deleteQueries;
    }

}
