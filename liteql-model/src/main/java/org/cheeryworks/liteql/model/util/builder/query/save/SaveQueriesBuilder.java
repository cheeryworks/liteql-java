package org.cheeryworks.liteql.model.util.builder.query.save;

import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;

import java.util.ArrayList;
import java.util.List;

public class SaveQueriesBuilder<T extends AbstractSaveQuery> extends SaveQueryBuilder<T> {

    private SaveQueryMetadata<T>[] saveQueryMetadataArray;

    public SaveQueriesBuilder(SaveQueryMetadata<T>... saveQueryMetadataArray) {
        this.saveQueryMetadataArray = saveQueryMetadataArray;
    }

    public List<T> getQueries() {
        List<T> saveQueries = new ArrayList<>();

        for (SaveQueryMetadata<T> saveQueryMetadata : saveQueryMetadataArray) {
            saveQueries.add(getQuery(saveQueryMetadata));
        }

        return saveQueries;
    }

}
