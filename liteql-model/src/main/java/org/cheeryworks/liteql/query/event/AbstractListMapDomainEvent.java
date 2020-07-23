package org.cheeryworks.liteql.query.event;

import org.cheeryworks.liteql.schema.TypeName;

import java.util.List;
import java.util.Map;

public abstract class AbstractListMapDomainEvent extends AbstractDomainEvent<List<Map<String, Object>>> {

    public AbstractListMapDomainEvent(List<Map<String, Object>> source, TypeName typeName) {
        super(source, typeName);
    }

    @Override
    public List<Map<String, Object>> getSource() {
        return super.getSource();
    }

}
