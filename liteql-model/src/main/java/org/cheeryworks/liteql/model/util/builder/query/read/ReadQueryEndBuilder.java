package org.cheeryworks.liteql.model.util.builder.query.read;

import org.apache.commons.lang3.ArrayUtils;
import org.cheeryworks.liteql.model.query.read.AbstractTypedReadQuery;
import org.cheeryworks.liteql.model.query.read.PageReadQuery;
import org.cheeryworks.liteql.model.query.read.TreeReadQuery;
import org.cheeryworks.liteql.model.util.builder.query.read.join.ReadQueryJoinMetadata;

import java.util.Arrays;
import java.util.LinkedList;

public class ReadQueryEndBuilder<T extends AbstractTypedReadQuery> {

    private T readQuery;

    private ReadQueryMetadata readQueryMetadata;

    public ReadQueryEndBuilder(ReadQueryMetadata readQueryMetadata, T readQuery) {
        this.readQueryMetadata = readQueryMetadata;
        this.readQuery = readQuery;
    }

    protected ReadQueryMetadata getReadQueryMetadata() {
        return readQueryMetadata;
    }

    protected T getReadQuery() {
        return readQuery;
    }

    public T getQuery() {
        readQuery.setDomainTypeName(readQueryMetadata.getDomainTypeName());
        readQuery.setFields(readQueryMetadata.getFields());

        if (ArrayUtils.isNotEmpty(readQueryMetadata.getReadQueryJoinMetadataArray())) {
            readQuery.setJoins(new LinkedList<>());

            for (ReadQueryJoinMetadata readQueryJoinMetadata : readQueryMetadata.getReadQueryJoinMetadataArray()) {
                readQuery.getJoins().add(readQueryMetadata.getJoinedQuery(readQueryJoinMetadata));
            }
        }

        readQuery.setConditions(readQueryMetadata.getConditions());
        readQuery.setSorts(readQueryMetadata.getSorts());
        readQuery.setScope(readQueryMetadata.getScope());

        if (readQuery instanceof TreeReadQuery) {
            ((TreeReadQuery) readQuery).setExpandLevel(readQueryMetadata.getExpandLevel());
        }

        if (readQuery instanceof PageReadQuery) {
            ((PageReadQuery) readQuery).setPage(this.readQueryMetadata.getPage());
            ((PageReadQuery) readQuery).setSize(this.readQueryMetadata.getSize());
        }

        if (readQueryMetadata.getAssociations() != null
                && readQueryMetadata.getAssociations().length > 0) {
            readQuery.setReferences(readQueryMetadata.getReferences());
            readQuery.setAssociations(Arrays.asList(readQueryMetadata.getAssociations()));
        }

        return readQuery;
    }

}