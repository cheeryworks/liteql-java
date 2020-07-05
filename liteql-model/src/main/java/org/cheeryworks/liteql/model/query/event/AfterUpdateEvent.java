package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.type.TypeName;

import java.util.List;
import java.util.Map;

public class AfterUpdateEvent extends AbstractWritableDomainEvent {

    public AfterUpdateEvent(List<Map<String, Object>> source, TypeName type, QueryType queryType) {
        super(source, type, queryType);
    }

}
