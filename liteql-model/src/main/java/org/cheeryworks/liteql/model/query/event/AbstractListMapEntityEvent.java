package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.enums.QueryType;

import java.util.List;
import java.util.Map;

public abstract class AbstractListMapEntityEvent extends EntityEvent<List<Map<String, Object>>> {

    public AbstractListMapEntityEvent(List<Map<String, Object>> source, Class type, QueryType queryType) {
        super(source, type, queryType);
    }

    @Override
    public List<Map<String, Object>> getSource() {
        return super.getSource();
    }

}
