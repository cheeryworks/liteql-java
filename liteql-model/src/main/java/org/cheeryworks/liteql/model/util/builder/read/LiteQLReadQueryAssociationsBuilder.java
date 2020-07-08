package org.cheeryworks.liteql.model.util.builder.read;

import org.cheeryworks.liteql.model.query.read.AbstractReadQuery;
import org.cheeryworks.liteql.model.util.builder.LiteQLQueryReference;

import java.util.List;

public class LiteQLReadQueryAssociationsBuilder<T extends AbstractReadQuery> extends LiteQLReadQueryEndBuilder<T> {

    private LiteQLReadQuery liteQLReadQuery;

    private Class<T> readQueryType;

    public LiteQLReadQueryAssociationsBuilder(LiteQLReadQuery liteQLReadQuery, Class<T> readQueryType) {
        super(liteQLReadQuery, readQueryType);

        this.liteQLReadQuery = liteQLReadQuery;
        this.readQueryType = readQueryType;
    }

    public LiteQLReadQueryEndBuilder<T> associations(List<LiteQLQueryReference> references, T... associations) {
        for (LiteQLQueryReference reference : references) {
            liteQLReadQuery.getReferences().put(reference.getSource(), reference.getDestination());
        }

        this.liteQLReadQuery.setAssociations(associations);

        return new LiteQLReadQueryEndBuilder<>(this.liteQLReadQuery, this.readQueryType);
    }

}
