package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.type.TypeName;

import java.util.List;
import java.util.Map;

public class AfterDeleteEvent extends AbstractWritableDomainEvent {

    public AfterDeleteEvent(List<Map<String, Object>> source, TypeName typeName, QueryType queryType) {
        super(source, typeName, queryType);
    }

}
