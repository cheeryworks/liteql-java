package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.type.TypeName;

import java.util.List;
import java.util.Map;

public class AfterReadEvent extends AbstractReadableDomainEvent {

    public AfterReadEvent(List<Map<String, Object>> source, TypeName typeName) {
        super(source, typeName);
    }

}
