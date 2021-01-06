package org.cheeryworks.liteql.query.event;

import org.cheeryworks.liteql.query.QueryContext;
import org.cheeryworks.liteql.query.enums.QueryPhase;
import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.schema.TypeName;

public abstract class AbstractQueryEvent<T> implements QueryEvent {

    private T source;

    private TypeName domainTypeName;

    private QueryType queryType;

    private QueryPhase queryPhase;

    private QueryContext queryContext;

    @Override
    public TypeName getDomainTypeName() {
        return domainTypeName;
    }

    @Override
    public QueryType getQueryType() {
        return queryType;
    }

    @Override
    public QueryPhase getQueryPhase() {
        return queryPhase;
    }

    @Override
    public QueryContext getQueryContext() {
        return queryContext;
    }

    public AbstractQueryEvent(
            T source, TypeName domainTypeName, QueryType queryType, QueryPhase queryPhase, QueryContext queryContext) {
        this.source = source;
        this.domainTypeName = domainTypeName;
        this.queryType = queryType;
        this.queryPhase = queryPhase;
        this.queryContext = queryContext;
    }

    @Override
    public T getSource() {
        return source;
    }

}
