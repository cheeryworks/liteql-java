package org.cheeryworks.liteql.util.builder.read;

import org.cheeryworks.liteql.model.query.read.AbstractReadQuery;
import org.cheeryworks.liteql.util.builder.LiteQLQueryReference;

public class LiteQLReadQueryReferencesBuilder<T extends AbstractReadQuery> extends LiteQLReadQueryEndBuilder<T> {

    private LiteQLReadQuery liteQLReadQuery;

    private Class<T> readQueryType;

    public LiteQLReadQueryReferencesBuilder(LiteQLReadQuery liteQLReadQuery, Class<T> readQueryType) {
        super(liteQLReadQuery, readQueryType);

        this.liteQLReadQuery = liteQLReadQuery;
        this.readQueryType = readQueryType;
    }

    public LiteQLReadQueryAssociationsBuilder<T> references(LiteQLQueryReference... liteQLQueryReferences) {
        for (LiteQLQueryReference liteQLQueryReference : liteQLQueryReferences) {
            this.liteQLReadQuery.getReferences().put(
                    liteQLQueryReference.getSource(), liteQLQueryReference.getDestination());
        }

        return new LiteQLReadQueryAssociationsBuilder<>(this.liteQLReadQuery, this.readQueryType);
    }

}
