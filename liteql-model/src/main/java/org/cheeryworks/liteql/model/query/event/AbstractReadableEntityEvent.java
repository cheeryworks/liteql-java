package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.enums.QueryType;

import java.util.List;
import java.util.Map;

public abstract class AbstractReadableEntityEvent extends AbstractListMapEntityEvent {

    public AbstractReadableEntityEvent(List<Map<String, Object>> source, Class type, QueryType queryType) {
        super(source, type, queryType);
    }

}
