package org.cheeryworks.liteql.query.event;

import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.util.LiteQLUtil;

import java.io.Serializable;

public abstract class AbstractQueryEvent<T> implements Serializable {

    private T source;

    private TypeName domainTypeName;

    private QueryType queryType;

    public TypeName getDomainTypeName() {
        return domainTypeName;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public AbstractQueryEvent(T source, TypeName domainTypeName, QueryType queryType) {
        this.source = source;
        this.domainTypeName = domainTypeName;
        this.queryType = queryType;
    }

    public T getSource() {
        return source;
    }

    @Override
    public String toString() {
        return LiteQLUtil.toJson(this);
    }

}
