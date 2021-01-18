package org.cheeryworks.liteql.skeleton.query.event;

import org.cheeryworks.liteql.skeleton.event.LiteQLEvent;
import org.cheeryworks.liteql.skeleton.query.QueryContext;
import org.cheeryworks.liteql.skeleton.query.enums.QueryPhase;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;
import org.cheeryworks.liteql.skeleton.schema.TypeName;

public interface QueryEvent extends LiteQLEvent {

    String QUERY_PHASE_KEY = "queryPhase";

    TypeName getDomainTypeName();

    QueryType getQueryType();

    QueryPhase getQueryPhase();

    QueryContext getQueryContext();

    Object getSource();

}
