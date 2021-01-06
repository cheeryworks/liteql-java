package org.cheeryworks.liteql.query.event;

import org.cheeryworks.liteql.query.QueryContext;
import org.cheeryworks.liteql.query.enums.QueryPhase;
import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.schema.TypeName;

import java.util.List;
import java.util.Map;

public abstract class AbstractListMapQueryEvent extends AbstractQueryEvent<List<Map<String, Object>>> {

    public AbstractListMapQueryEvent(
            List<Map<String, Object>> source, TypeName typeName,
            QueryType queryType, QueryPhase queryPhase, QueryContext queryContext) {
        super(source, typeName, queryType, queryPhase, queryContext);
    }

}
