package org.cheeryworks.liteql.query.event;

import org.cheeryworks.liteql.query.QueryContext;
import org.cheeryworks.liteql.query.enums.QueryPhase;
import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.schema.TypeName;

import java.io.Serializable;

public interface QueryEvent extends Serializable {

    String QUERY_PHASE_KEY = "queryPhase";

    TypeName getDomainTypeName();

    QueryType getQueryType();

    QueryPhase getQueryPhase();

    QueryContext getQueryContext();

    Object getSource();

}
