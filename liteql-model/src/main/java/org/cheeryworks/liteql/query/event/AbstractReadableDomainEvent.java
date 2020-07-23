package org.cheeryworks.liteql.query.event;

import org.cheeryworks.liteql.schema.TypeName;

import java.util.List;
import java.util.Map;

public abstract class AbstractReadableDomainEvent extends AbstractListMapDomainEvent {

    public AbstractReadableDomainEvent(List<Map<String, Object>> source, TypeName typeName) {
        super(source, typeName);
    }

}
