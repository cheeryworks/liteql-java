package org.cheeryworks.liteql.query.event;

import org.cheeryworks.liteql.query.enums.QueryPhase;
import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.schema.TypeName;

import java.util.List;
import java.util.Map;

public abstract class AbstractReadableQueryEvent extends AbstractListMapQueryEvent {

    public AbstractReadableQueryEvent(
            List<Map<String, Object>> source, TypeName typeName, QueryType queryType, QueryPhase queryPhase) {
        super(source, typeName, queryType, queryPhase);
    }

}
