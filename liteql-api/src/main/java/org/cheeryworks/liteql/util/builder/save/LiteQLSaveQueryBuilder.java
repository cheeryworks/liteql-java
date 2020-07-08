package org.cheeryworks.liteql.util.builder.save;

import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.model.query.save.SaveQueryAssociations;

import java.util.LinkedList;
import java.util.List;

public class LiteQLSaveQueryBuilder<T extends AbstractSaveQuery> {

    protected T getQuery(LiteQLSaveQuery<T> liteQLSaveQuery) {
        T saveQuery = liteQLSaveQuery.getSaveQuery();

        saveQuery.setDomainTypeName(liteQLSaveQuery.getDomainType());
        saveQuery.setData(liteQLSaveQuery.getData());

        if (liteQLSaveQuery.getReferences() != null && liteQLSaveQuery.getReferences().size() > 0) {
            saveQuery.setReferences(liteQLSaveQuery.getReferences());
            saveQuery.setAssociations(
                    new SaveQueryAssociations(
                            getQueries(liteQLSaveQuery.getAssociations())));
        }

        return saveQuery;
    }

    private List<AbstractSaveQuery> getQueries(LiteQLSaveQuery[] liteQLSaveQueryEndBuilders) {
        List<AbstractSaveQuery> saveQueries = new LinkedList<>();

        for (LiteQLSaveQuery liteQLSaveQuery : liteQLSaveQueryEndBuilders) {
            saveQueries.add(getQuery(liteQLSaveQuery));
        }

        return saveQueries;
    }

}
