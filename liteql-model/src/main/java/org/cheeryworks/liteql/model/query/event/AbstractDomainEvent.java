package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.type.Type;

import java.io.Serializable;

public abstract class AbstractDomainEvent<T> implements Serializable {

    private T source;

    private Type type;

    public Type getType() {
        return type;
    }

    public AbstractDomainEvent(T source, Type type) {
        this.source = source;
        this.type = type;
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
