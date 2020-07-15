package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.AbstractReadQuery;
import org.cheeryworks.liteql.model.util.builder.query.QueryReference;

public class ReadQueryReferencesBuilder<T extends AbstractReadQuery> extends ReadQueryEndBuilder<T> {

    private ReadQueryMetadata readQueryMetadata;

    private Class<T> readQueryType;

    public ReadQueryReferencesBuilder(ReadQueryMetadata readQueryMetadata, Class<T> readQueryType) {
        super(readQueryMetadata, readQueryType);

        this.readQueryMetadata = readQueryMetadata;
        this.readQueryType = readQueryType;
    }

    public ReadQueryAssociationsBuilder<T> references(QueryReference... queryReferences) {
        for (QueryReference queryReference : queryReferences) {
            this.readQueryMetadata.getReferences().put(
                    queryReference.getSource(), queryReference.getDestination());
        }

        return new ReadQueryAssociationsBuilder<>(this.readQueryMetadata, this.readQueryType);
    }

}
