package org.cheeryworks.liteql.model.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbstractDomainQuery implements Query {

    public static final String DOMAIN_TYPE_KEY = "domainType";

    @JsonProperty(required = true)
    private String domainType;

    @JsonIgnore
    private QueryConditions accessDecisionConditions;

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public QueryConditions getAccessDecisionConditions() {
        return accessDecisionConditions;
    }

    public void setAccessDecisionConditions(QueryConditions accessDecisionConditions) {
        this.accessDecisionConditions = accessDecisionConditions;
    }

}
