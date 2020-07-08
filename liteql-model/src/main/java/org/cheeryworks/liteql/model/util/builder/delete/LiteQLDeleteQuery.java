package org.cheeryworks.liteql.model.util.builder.delete;

import org.cheeryworks.liteql.model.query.QueryConditions;
import org.cheeryworks.liteql.model.type.TypeName;

public class LiteQLDeleteQuery {

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

    public static LiteQLDeleteQueryConditionsBuilder delete(TypeName domainTypeName) {
        LiteQLDeleteQuery liteQLDeleteQuery = new LiteQLDeleteQuery();
        liteQLDeleteQuery.setDomainTypeName(domainTypeName);

        return new LiteQLDeleteQueryConditionsBuilder(liteQLDeleteQuery);
    }

}
