package org.cheeryworks.liteql.query.event;

import org.cheeryworks.liteql.schema.TypeName;

import java.io.Serializable;

public abstract class AbstractDomainEvent<T> implements Serializable {

    private T source;

    private TypeName typeName;

    public TypeName getTypeName() {
        return typeName;
    }

    public AbstractDomainEvent(T source, TypeName typeName) {
        this.source = source;
        this.typeName = typeName;
    }

    public T getSource() {
        return source;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{"
                + "typeName=" + typeName
                + ", source=" + source
                + '}';
    }

}
