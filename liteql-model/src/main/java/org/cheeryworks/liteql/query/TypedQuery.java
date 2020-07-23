package org.cheeryworks.liteql.query;

import org.cheeryworks.liteql.query.enums.QueryType;

public interface TypedQuery extends Query {

    String QUERY_TYPE_KEY = "queryType";

    QueryType getQueryType();

}
