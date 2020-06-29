package org.cheeryworks.liteql.util.builder.save;

import org.cheeryworks.liteql.model.query.save.AbstractSaveQuery;
import org.cheeryworks.liteql.util.builder.LiteQLQueryReference;

import java.util.List;

public class LiteQLSaveQueryAssociationsBuilder<T extends AbstractSaveQuery> extends LiteQLSaveQueryEndBuilder<T> {

    private LiteQLSaveQuery<T> liteQLSaveQuery;

    public LiteQLSaveQueryAssociationsBuilder(LiteQLSaveQuery<T> liteQLSaveQuery) {
        super(liteQLSaveQuery);

        this.liteQLSaveQuery = liteQLSaveQuery;
    }

    public LiteQLSaveQueryEndBuilder<T> associations(
            List<LiteQLQueryReference> references, LiteQLSaveQuery... liteQLSaveQueries) {
        for (LiteQLQueryReference reference : references) {
            liteQLSaveQuery.getReferences().put(reference.getSource(), reference.getDestination());
        }

        liteQLSaveQuery.setAssociations(liteQLSaveQueries);

        return new LiteQLSaveQueryEndBuilder<>(liteQLSaveQuery);
    }

}
