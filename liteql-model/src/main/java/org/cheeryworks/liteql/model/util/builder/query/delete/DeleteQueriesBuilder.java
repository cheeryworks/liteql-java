package org.cheeryworks.liteql.model.util.builder.query.delete;

import org.cheeryworks.liteql.model.query.delete.DeleteQuery;

import java.util.LinkedList;
import java.util.List;

public class DeleteQueriesBuilder extends DeleteQueryBuilder {

    private DeleteQueryMetadata[] liteQLDeleteQueries;

    public DeleteQueriesBuilder(DeleteQueryMetadata... liteQLDeleteQueries) {
        this.liteQLDeleteQueries = liteQLDeleteQueries;
    }

    public List<DeleteQuery> getQueries() {
        List<DeleteQuery> deleteQueries = new LinkedList<>();

        for (DeleteQueryMetadata deleteQueryMetadata : liteQLDeleteQueries) {
            deleteQueries.add(getQuery(deleteQueryMetadata));
        }

        return deleteQueries;
    }

}
