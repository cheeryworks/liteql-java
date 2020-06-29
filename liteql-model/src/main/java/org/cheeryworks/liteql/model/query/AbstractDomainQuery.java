package org.cheeryworks.liteql.model.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cheeryworks.liteql.model.type.DomainTypeName;

public abstract class AbstractDomainQuery implements Query {

    @JsonProperty(required = true)
    private DomainTypeName domainTypeName;

    @JsonIgnore
    private QueryConditions accessDecisionConditions;

    public DomainTypeName getDomainTypeName() {
        return domainTypeName;
    }

    public void setDomainTypeName(DomainTypeName domainTypeName) {
        this.domainTypeName = domainTypeName;
    }

    public QueryConditions getAccessDecisionConditions() {
        return accessDecisionConditions;
    }

    public void setAccessDecisionConditions(QueryConditions accessDecisionConditions) {
        this.accessDecisionConditions = accessDecisionConditions;
    }

}
