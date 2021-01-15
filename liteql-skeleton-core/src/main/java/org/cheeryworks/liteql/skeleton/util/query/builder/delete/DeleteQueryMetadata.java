package org.cheeryworks.liteql.skeleton.util.query.builder.delete;

import org.cheeryworks.liteql.skeleton.query.QueryConditions;
import org.cheeryworks.liteql.skeleton.schema.TypeName;

public class DeleteQueryMetadata {

    private TypeName domainTypeName;

    private QueryConditions conditions = new QueryConditions();

    private boolean truncated;

    public TypeName getDomainTypeName() {
        return domainTypeName;
    }

    public void setDomainTypeName(TypeName domainTypeName) {
        this.domainTypeName = domainTypeName;
    }

    public QueryConditions getConditions() {
        return conditions;
    }

    public void setConditions(QueryConditions conditions) {
        this.conditions = conditions;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

}
