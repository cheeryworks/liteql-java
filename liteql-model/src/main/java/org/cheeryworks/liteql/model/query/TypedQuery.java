package org.cheeryworks.liteql.model.query;

import org.cheeryworks.liteql.model.enums.QueryType;

public interface TypedQuery extends Query {

    String QUERY_TYPE_KEY = "queryType";

    QueryType getQueryType();

}
