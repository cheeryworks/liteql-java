package org.cheeryworks.liteql.skeleton.util.query.builder.save;

import org.cheeryworks.liteql.skeleton.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.skeleton.query.save.SaveQueryAssociations;

import java.util.ArrayList;
import java.util.List;

public class SaveQueryBuilder<T extends AbstractSaveQuery> {

    protected T getQuery(SaveQueryMetadata<T> saveQueryMetadata) {
        T saveQuery = saveQueryMetadata.getSaveQuery();

        saveQuery.setDomainTypeName(saveQueryMetadata.getDomainTypeName());
        saveQuery.setData(saveQueryMetadata.getData());

        if (saveQueryMetadata.getReferences() != null && saveQueryMetadata.getReferences().size() > 0) {
            saveQuery.setReferences(saveQueryMetadata.getReferences());
            saveQuery.setAssociations(
                    new SaveQueryAssociations(
                            getQueries(saveQueryMetadata.getAssociations())));
        }

        return saveQuery;
    }

    private List<AbstractSaveQuery> getQueries(SaveQueryMetadata[] saveQueryMetadataEndBuilders) {
        List<AbstractSaveQuery> saveQueries = new ArrayList<>();

        for (SaveQueryMetadata saveQueryMetadata : saveQueryMetadataEndBuilders) {
            saveQueries.add(getQuery(saveQueryMetadata));
        }

        return saveQueries;
    }

}
