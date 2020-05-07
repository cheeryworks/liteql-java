package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.enums.QueryType;

import java.util.List;
import java.util.Map;

public abstract class AbstractWritableEntityEvent extends AbstractListMapEntityEvent {

    public AbstractWritableEntityEvent(List<Map<String, Object>> source, Class type, QueryType queryType) {
        super(source, type, queryType);
    }

}
