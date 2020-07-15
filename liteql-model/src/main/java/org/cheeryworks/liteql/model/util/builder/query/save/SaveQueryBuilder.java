package org.cheeryworks.liteql.model.util.builder.query.save;

import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.query.save.SaveQueryAssociations;

import java.util.LinkedList;
import java.util.List;

public class SaveQueryBuilder<T extends AbstractSaveQuery> {

    protected T getQuery(SaveQueryMetadata<T> saveQueryMetadataArray) {
        T saveQuery = saveQueryMetadataArray.getSaveQuery();

        saveQuery.setDomainTypeName(saveQueryMetadataArray.getDomainTypeName());
        saveQuery.setData(saveQueryMetadataArray.getData());

        if (saveQueryMetadataArray.getReferences() != null && saveQueryMetadataArray.getReferences().size() > 0) {
            saveQuery.setReferences(saveQueryMetadataArray.getReferences());
            saveQuery.setAssociations(
                    new SaveQueryAssociations(
                            getQueries(saveQueryMetadataArray.getAssociations())));
        }

        return saveQuery;
    }

    private List<AbstractSaveQuery> getQueries(SaveQueryMetadata[] saveQueryMetadataEndBuilders) {
        List<AbstractSaveQuery> saveQueries = new LinkedList<>();

        for (SaveQueryMetadata saveQueryMetadata : saveQueryMetadataEndBuilders) {
            saveQueries.add(getQuery(saveQueryMetadata));
        }

        return saveQueries;
    }

}
