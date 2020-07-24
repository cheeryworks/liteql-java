package org.cheeryworks.liteql.query.event;

import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.schema.TypeName;

import java.util.List;
import java.util.Map;

public abstract class AbstractWritableDomainEvent extends AbstractListMapDomainEvent {

    private QueryType queryType;

    public QueryType getQueryType() {
        return queryType;
    }

    public AbstractWritableDomainEvent(List<Map<String, Object>> source, TypeName typeName, QueryType queryType) {
        super(source, typeName);

        this.queryType = queryType;
    }

}
