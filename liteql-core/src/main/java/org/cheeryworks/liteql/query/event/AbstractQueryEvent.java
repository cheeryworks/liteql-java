package org.cheeryworks.liteql.query.event;

import org.cheeryworks.liteql.query.enums.QueryPhase;
import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.schema.TypeName;

public abstract class AbstractQueryEvent<T> implements QueryEvent {

    private T source;

    private TypeName domainTypeName;

    private QueryType queryType;

    private QueryPhase queryPhase;

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

    public AbstractQueryEvent(T source, TypeName domainTypeName, QueryType queryType, QueryPhase queryPhase) {
        this.source = source;
        this.domainTypeName = domainTypeName;
        this.queryType = queryType;
        this.queryPhase = queryPhase;
    }

    @Override
    public T getSource() {
        return source;
    }

}
