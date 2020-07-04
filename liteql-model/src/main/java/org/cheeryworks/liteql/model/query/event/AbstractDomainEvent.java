package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.type.DomainTypeName;

import java.io.Serializable;

public abstract class AbstractDomainEvent<T> implements Serializable {

    private T source;

    private DomainTypeName type;

    public DomainTypeName getType() {
        return type;
    }

    public AbstractDomainEvent(T source, DomainTypeName type) {
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
