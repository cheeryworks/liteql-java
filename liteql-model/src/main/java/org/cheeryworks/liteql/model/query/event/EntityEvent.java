package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.enums.QueryType;

import java.io.Serializable;

public abstract class EntityEvent<T> implements Serializable {

    private T source;

    private Class<T> type;

    private QueryType queryType;

    public Class<T> getType() {
        return type;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public EntityEvent(T source, Class<T> type, QueryType queryType) {
        this.source = source;
        this.type = type;
        this.queryType = queryType;
    }

    public T getSource() {
        return source;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{"
                + "type=" + type
                + ", source=" + source
                + '}';
    }

}
