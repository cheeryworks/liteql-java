package org.cheeryworks.liteql.query.event;

import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.schema.TypeName;

import java.util.List;
import java.util.Map;

public class AfterDeleteEvent extends AbstractWritableDomainEvent {

    public AfterDeleteEvent(List<Map<String, Object>> source, TypeName typeName, QueryType queryType) {
        super(source, typeName, queryType);
    }

}
