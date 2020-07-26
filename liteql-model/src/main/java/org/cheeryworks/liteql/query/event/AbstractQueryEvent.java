package org.cheeryworks.liteql.query.event;

import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.schema.TypeName;
import org.cheeryworks.liteql.util.LiteQLUtil;

import java.io.Serializable;

public abstract class AbstractQueryEvent<T> implements Serializable {

    private T source;

    private TypeName typeName;

    private QueryType queryType;

    public TypeName getTypeName() {
        return typeName;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public AbstractQueryEvent(T source, TypeName typeName, QueryType queryType) {
        this.source = source;
        this.typeName = typeName;
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
