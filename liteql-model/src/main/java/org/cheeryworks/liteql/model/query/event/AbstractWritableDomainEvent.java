package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.type.TypeName;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

public abstract class AbstractWritableDomainEvent extends AbstractListMapDomainEvent {

    private QueryType queryType;

    public QueryType getQueryType() {
        return queryType;
    }

    public AbstractWritableDomainEvent(List<Map<String, Object>> source, TypeName type, QueryType queryType) {
        super(source, type);

        Assert.notNull(queryType, "QueryType can not be null");

        this.queryType = queryType;
    }

}
