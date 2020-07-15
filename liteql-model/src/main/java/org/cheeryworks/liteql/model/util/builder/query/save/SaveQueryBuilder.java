package org.cheeryworks.liteql.model.util.builder.query.save;

import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.query.save.SaveQueryAssociations;

import java.util.LinkedList;
import java.util.List;

public class SaveQueryBuilder<T extends AbstractSaveQuery> {

    protected T getQuery(SaveQueryMetadata<T> liteQLSaveQueryMetadata) {
        T saveQuery = liteQLSaveQueryMetadata.getSaveQuery();

        saveQuery.setDomainTypeName(liteQLSaveQueryMetadata.getDomainTypeName());
        saveQuery.setData(liteQLSaveQueryMetadata.getData());

        if (liteQLSaveQueryMetadata.getReferences() != null && liteQLSaveQueryMetadata.getReferences().size() > 0) {
            saveQuery.setReferences(liteQLSaveQueryMetadata.getReferences());
            saveQuery.setAssociations(
                    new SaveQueryAssociations(
                            getQueries(liteQLSaveQueryMetadata.getAssociations())));
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
