package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.type.Type;

import java.util.List;
import java.util.Map;

public abstract class AbstractReadableDomainEvent extends AbstractListMapDomainEvent {

    public AbstractReadableDomainEvent(List<Map<String, Object>> source, Type type) {
        super(source, type);
    }

}
