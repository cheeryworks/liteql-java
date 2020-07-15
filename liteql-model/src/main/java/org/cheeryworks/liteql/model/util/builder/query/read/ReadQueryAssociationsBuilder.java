package org.cheeryworks.liteql.model.util.builder.query.read;

import org.cheeryworks.liteql.model.query.read.AbstractReadQuery;
import org.cheeryworks.liteql.model.util.builder.query.QueryReference;

import java.util.List;

public class ReadQueryAssociationsBuilder<T extends AbstractReadQuery> extends ReadQueryEndBuilder<T> {

    private ReadQueryMetadata readQueryMetadata;

    private Class<T> readQueryType;

    public ReadQueryAssociationsBuilder(ReadQueryMetadata readQueryMetadata, Class<T> readQueryType) {
        super(readQueryMetadata, readQueryType);

        this.readQueryMetadata = readQueryMetadata;
        this.readQueryType = readQueryType;
    }

    public ReadQueryEndBuilder<T> associations(List<QueryReference> references, T... associations) {
        for (QueryReference reference : references) {
            readQueryMetadata.getReferences().put(reference.getSource(), reference.getDestination());
        }

        this.readQueryMetadata.setAssociations(associations);

        return new ReadQueryEndBuilder<>(this.readQueryMetadata, this.readQueryType);
    }

}
