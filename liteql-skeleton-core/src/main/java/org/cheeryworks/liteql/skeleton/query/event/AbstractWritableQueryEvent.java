package org.cheeryworks.liteql.skeleton.query.event;

import org.cheeryworks.liteql.skeleton.query.QueryContext;
import org.cheeryworks.liteql.skeleton.query.enums.QueryPhase;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;
import org.cheeryworks.liteql.skeleton.schema.TypeName;

import java.util.List;
import java.util.Map;

public abstract class AbstractWritableQueryEvent extends AbstractListMapQueryEvent {

    public AbstractWritableQueryEvent(
            List<Map<String, Object>> source, TypeName typeName,
            QueryType queryType, QueryPhase queryPhase, QueryContext queryContext) {
        super(source, typeName, queryType, queryPhase, queryContext);
    }

}
