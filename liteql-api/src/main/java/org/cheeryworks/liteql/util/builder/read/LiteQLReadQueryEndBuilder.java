package org.cheeryworks.liteql.util.builder.read;

import org.apache.commons.lang3.ArrayUtils;
import org.cheeryworks.liteql.model.query.read.AbstractReadQuery;
import org.cheeryworks.liteql.model.query.read.PageReadQuery;
import org.cheeryworks.liteql.model.query.read.TreeReadQuery;
import org.cheeryworks.liteql.util.builder.read.join.LiteQLReadQueryJoin;

import java.util.Arrays;
import java.util.LinkedList;

public class LiteQLReadQueryEndBuilder<T extends AbstractReadQuery> {

    private LiteQLReadQuery liteQLReadQuery;

    private T query;

    public LiteQLReadQueryEndBuilder(LiteQLReadQuery liteQLReadQuery, Class<T> readQueryType) {
        this.liteQLReadQuery = liteQLReadQuery;

        try {
            this.query = readQueryType.newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    public T getQuery() {
        this.query.setDomainTypeName(liteQLReadQuery.getDomainType());
        this.query.setFields(liteQLReadQuery.getFields());

        if (ArrayUtils.isNotEmpty(liteQLReadQuery.getLiteQLReadQueryJoins())) {
            this.query.setJoins(new LinkedList<>());

            for (LiteQLReadQueryJoin liteQLReadQueryJoin : liteQLReadQuery.getLiteQLReadQueryJoins()) {
                this.query.getJoins().add(liteQLReadQuery.getJoinedQuery(liteQLReadQueryJoin));
            }
        }

        this.query.setConditions(liteQLReadQuery.getConditions());
        this.query.setSorts(liteQLReadQuery.getSorts());
        this.query.setScope(liteQLReadQuery.getScope());

        if (query instanceof TreeReadQuery) {
            ((TreeReadQuery) query).setExpandLevel(liteQLReadQuery.getExpandLevel());
        }

        if (query instanceof PageReadQuery) {
            ((PageReadQuery) query).setPage(this.liteQLReadQuery.getPage());
            ((PageReadQuery) query).setSize(this.liteQLReadQuery.getSize());
        }

        if (liteQLReadQuery.getAssociations() != null
                && liteQLReadQuery.getAssociations().length > 0) {
            this.query.setReferences(liteQLReadQuery.getReferences());
            this.query.setAssociations(Arrays.asList(liteQLReadQuery.getAssociations()));
        }

        return this.query;
    }

}
