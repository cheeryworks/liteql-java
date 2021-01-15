package org.cheeryworks.liteql.skeleton.util.query.builder.read;

import org.cheeryworks.liteql.skeleton.query.read.AbstractTypedReadQuery;
import org.cheeryworks.liteql.skeleton.util.query.builder.QueryReference;

import java.util.List;

public class ReadQueryAssociationsBuilder<T extends AbstractTypedReadQuery> extends ReadQueryEndBuilder<T> {

    public ReadQueryAssociationsBuilder(ReadQueryMetadata readQueryMetadata, T readQuery) {
        super(readQueryMetadata, readQuery);
    }

    public ReadQueryEndBuilder<T> associations(List<QueryReference> references, T... associations) {
        for (QueryReference reference : references) {
            getReadQueryMetadata().getReferences().put(reference.getSource(), reference.getDestination());
        }

        getReadQueryMetadata().setAssociations(associations);

        return this;
    }

}
