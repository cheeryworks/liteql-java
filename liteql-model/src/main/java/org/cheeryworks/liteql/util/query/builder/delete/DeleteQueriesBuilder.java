package org.cheeryworks.liteql.util.query.builder.delete;

import org.cheeryworks.liteql.query.delete.DeleteQuery;

import java.util.LinkedList;
import java.util.List;

public class DeleteQueriesBuilder extends DeleteQueryBuilder {

    private DeleteQueryMetadata[] deleteQueryMetadataArray;

    public DeleteQueriesBuilder(DeleteQueryMetadata... deleteQueryMetadataArray) {
        this.deleteQueryMetadataArray = deleteQueryMetadataArray;
    }

    public List<DeleteQuery> getQueries() {
        List<DeleteQuery> deleteQueries = new LinkedList<>();

        for (DeleteQueryMetadata deleteQueryMetadata : deleteQueryMetadataArray) {
            deleteQueries.add(getQuery(deleteQueryMetadata));
        }

        return deleteQueries;
    }

}
