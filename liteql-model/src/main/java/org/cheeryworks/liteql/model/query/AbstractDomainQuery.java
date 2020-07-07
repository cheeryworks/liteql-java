package org.cheeryworks.liteql.model.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cheeryworks.liteql.model.type.Type;

public abstract class AbstractDomainQuery implements Query {

    @JsonProperty(required = true)
    private Type domainType;

    @JsonIgnore
    private QueryConditions accessDecisionConditions;

    public Type getDomainType() {
        return domainType;
    }

    public void setDomainType(Type domainType) {
        this.domainType = domainType;
    }

    public QueryConditions getAccessDecisionConditions() {
        return accessDecisionConditions;
    }

    public void setAccessDecisionConditions(QueryConditions accessDecisionConditions) {
        this.accessDecisionConditions = accessDecisionConditions;
    }

}
