package org.cheeryworks.liteql.model.query.event;

import org.cheeryworks.liteql.model.enums.QueryType;

import java.util.List;
import java.util.Map;

public class BeforeDeleteEvent extends AbstractWritableEntityEvent {

    public BeforeDeleteEvent(List<Map<String, Object>> source, Class type, QueryType queryType) {
        super(source, type, queryType);
    }

}