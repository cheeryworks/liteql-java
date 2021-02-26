package org.cheeryworks.liteql.skeleton.util.query.builder.delete;

import org.cheeryworks.liteql.skeleton.query.delete.DeleteQuery;

import java.util.ArrayList;
import java.util.List;

public class DeleteQueriesBuilder extends DeleteQueryBuilder {

    private DeleteQueryMetadata[] deleteQueryMetadataArray;

    public DeleteQueriesBuilder(DeleteQueryMetadata... deleteQueryMetadataArray) {
        this.deleteQueryMetadataArray = deleteQueryMetadataArray;
    }

    public List<DeleteQuery> getQueries() {
        List<DeleteQuery> deleteQueries = new ArrayList<>();

        for (DeleteQueryMetadata deleteQueryMetadata : deleteQueryMetadataArray) {
            deleteQueries.add(getQuery(deleteQueryMetadata));
        }

        return deleteQueries;
    }

}
