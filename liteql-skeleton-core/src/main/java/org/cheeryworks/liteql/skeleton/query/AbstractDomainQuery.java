package org.cheeryworks.liteql.skeleton.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cheeryworks.liteql.skeleton.schema.TypeName;

public abstract class AbstractDomainQuery implements DomainQuery {

    @JsonProperty(required = true)
    private TypeName domainTypeName;

    @JsonIgnore
    private QueryConditions accessDecisionConditions;

    @Override
    public TypeName getDomainTypeName() {
        return domainTypeName;
    }

    public void setDomainTypeName(TypeName domainTypeName) {
        this.domainTypeName = domainTypeName;
    }

    @Override
    public QueryConditions getAccessDecisionConditions() {
        return accessDecisionConditions;
    }

    public void setAccessDecisionConditions(QueryConditions accessDecisionConditions) {
        this.accessDecisionConditions = accessDecisionConditions;
    }

}
