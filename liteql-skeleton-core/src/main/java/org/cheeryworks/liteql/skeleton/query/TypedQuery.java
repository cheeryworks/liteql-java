package org.cheeryworks.liteql.skeleton.query;

import org.cheeryworks.liteql.skeleton.query.enums.QueryType;

public interface TypedQuery extends Query {

    String QUERY_TYPE_KEY = "queryType";

    QueryType getQueryType();

}
