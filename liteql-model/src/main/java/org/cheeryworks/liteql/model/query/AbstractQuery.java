package org.cheeryworks.liteql.model.query;

import org.cheeryworks.liteql.model.enums.QueryType;

import java.io.Serializable;

public abstract class AbstractQuery implements Serializable {

    public static final String QUERY_TYPE_KEY = "queryType";

    private QueryType queryType;

    private String domainType;

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

}
