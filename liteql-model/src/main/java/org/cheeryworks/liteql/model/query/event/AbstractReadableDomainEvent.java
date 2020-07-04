package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.type.DomainTypeName;

import java.util.List;
import java.util.Map;

public abstract class AbstractReadableDomainEvent extends AbstractListMapDomainEvent {

    public AbstractReadableDomainEvent(List<Map<String, Object>> source, DomainTypeName type) {
        super(source, type);
    }

}
