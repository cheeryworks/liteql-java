package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.type.DomainTypeName;

import java.util.List;
import java.util.Map;

public abstract class AbstractListMapDomainEvent extends AbstractDomainEvent<List<Map<String, Object>>> {

    public AbstractListMapDomainEvent(List<Map<String, Object>> source, DomainTypeName type) {
        super(source, type);
    }

    @Override
    public List<Map<String, Object>> getSource() {
        return super.getSource();
    }

}
