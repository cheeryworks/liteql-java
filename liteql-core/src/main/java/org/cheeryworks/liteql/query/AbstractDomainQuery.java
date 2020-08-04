package org.cheeryworks.liteql.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cheeryworks.liteql.schema.TypeName;

public abstract class AbstractDomainQuery implements Query {

    @JsonProperty(required = true)
    private TypeName domainTypeName;

    @JsonIgnore
    private QueryConditions accessDecisionConditions;

    public TypeName getDomainTypeName() {
        return domainTypeName;
    }

    public void setDomainTypeName(TypeName domainTypeName) {
        this.domainTypeName = domainTypeName;
    }

    public QueryConditions getAccessDecisionConditions() {
        return accessDecisionConditions;
    }

    public void setAccessDecisionConditions(QueryConditions accessDecisionConditions) {
        this.accessDecisionConditions = accessDecisionConditions;
    }

}
