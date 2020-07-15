package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.AbstractReadQuery;
import org.cheeryworks.liteql.model.util.builder.query.QueryReference;

import java.util.List;

public class ReadQueryAssociationsBuilder<T extends AbstractReadQuery> extends ReadQueryEndBuilder<T> {

    private ReadQueryMetadata liteQLReadQuery;

    private Class<T> readQueryType;

    public ReadQueryAssociationsBuilder(ReadQueryMetadata liteQLReadQuery, Class<T> readQueryType) {
        super(liteQLReadQuery, readQueryType);

        this.liteQLReadQuery = liteQLReadQuery;
        this.readQueryType = readQueryType;
    }

    public ReadQueryEndBuilder<T> associations(List<QueryReference> references, T... associations) {
        for (QueryReference reference : references) {
            liteQLReadQuery.getReferences().put(reference.getSource(), reference.getDestination());
        }

        this.liteQLReadQuery.setAssociations(associations);

        return new ReadQueryEndBuilder<>(this.liteQLReadQuery, this.readQueryType);
    }

}
