package org.cheeryworks.liteql.model.util.builder.query.read;

import org.apache.commons.lang3.ArrayUtils;
import org.cheeryworks.liteql.model.query.read.AbstractReadQuery;
import org.cheeryworks.liteql.model.query.read.PageReadQuery;
import org.cheeryworks.liteql.model.query.read.TreeReadQuery;
import org.cheeryworks.liteql.model.util.builder.query.read.join.ReadQueryJoinMetadata;

import java.util.Arrays;
import java.util.LinkedList;

public class ReadQueryEndBuilder<T extends AbstractReadQuery> {

    private ReadQueryMetadata readQueryMetadata;

    private T query;

    public ReadQueryEndBuilder(ReadQueryMetadata readQueryMetadata, Class<T> readQueryType) {
        this.readQueryMetadata = readQueryMetadata;

        try {
            this.query = readQueryType.newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    public T getQuery() {
        this.query.setDomainTypeName(readQueryMetadata.getDomainTypeName());
        this.query.setFields(readQueryMetadata.getFields());

        if (ArrayUtils.isNotEmpty(readQueryMetadata.getReadQueryJoinMetadataArray())) {
            this.query.setJoins(new LinkedList<>());

            for (ReadQueryJoinMetadata readQueryJoinMetadata : readQueryMetadata.getReadQueryJoinMetadataArray()) {
                this.query.getJoins().add(readQueryMetadata.getJoinedQuery(readQueryJoinMetadata));
            }
        }

        this.query.setConditions(readQueryMetadata.getConditions());
        this.query.setSorts(readQueryMetadata.getSorts());
        this.query.setScope(readQueryMetadata.getScope());

        if (query instanceof TreeReadQuery) {
            ((TreeReadQuery) query).setExpandLevel(readQueryMetadata.getExpandLevel());
        }

        if (query instanceof PageReadQuery) {
            ((PageReadQuery) query).setPage(this.readQueryMetadata.getPage());
            ((PageReadQuery) query).setSize(this.readQueryMetadata.getSize());
        }

        if (readQueryMetadata.getAssociations() != null
                && readQueryMetadata.getAssociations().length > 0) {
            this.query.setReferences(readQueryMetadata.getReferences());
            this.query.setAssociations(Arrays.asList(readQueryMetadata.getAssociations()));
        }

        return this.query;
    }

}
