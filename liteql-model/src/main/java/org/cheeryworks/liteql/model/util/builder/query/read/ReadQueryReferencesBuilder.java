package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.AbstractReadQuery;
import org.cheeryworks.liteql.model.util.builder.query.QueryReference;

public class ReadQueryReferencesBuilder<T extends AbstractReadQuery> extends ReadQueryEndBuilder<T> {

    private ReadQueryMetadata liteQLReadQuery;

    private Class<T> readQueryType;

    public ReadQueryReferencesBuilder(ReadQueryMetadata liteQLReadQuery, Class<T> readQueryType) {
        super(liteQLReadQuery, readQueryType);

        this.liteQLReadQuery = liteQLReadQuery;
        this.readQueryType = readQueryType;
    }

    public ReadQueryAssociationsBuilder<T> references(QueryReference... queryReferences) {
        for (QueryReference queryReference : queryReferences) {
            this.liteQLReadQuery.getReferences().put(
                    queryReference.getSource(), queryReference.getDestination());
        }

        return new ReadQueryAssociationsBuilder<>(this.liteQLReadQuery, this.readQueryType);
    }

}
