package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.enums.QueryType;

import java.util.Map;

public abstract class AbstractMapEntityEvent extends EntityEvent<Map<String, Object>> {

    public AbstractMapEntityEvent(Map<String, Object> source, Class type, QueryType queryType) {
        super(source, type, queryType);
    }

    @Override
    public Map<String, Object> getSource() {
        return super.getSource();
    }

}
