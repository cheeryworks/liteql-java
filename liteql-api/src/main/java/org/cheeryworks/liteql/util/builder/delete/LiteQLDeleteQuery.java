package org.cheeryworks.liteql.util.builder.delete;

import org.cheeryworks.liteql.model.query.QueryConditions;
import org.cheeryworks.liteql.model.type.DomainType;

public class LiteQLDeleteQuery {

    private DomainType domainType;

    private QueryConditions conditions = new QueryConditions();

    private boolean truncated;

    public DomainType getDomainType() {
        return domainType;
    }

    public void setDomainType(DomainType domainType) {
        this.domainType = domainType;
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

    public static LiteQLDeleteQueryConditionsBuilder delete(DomainType domainType) {
        LiteQLDeleteQuery liteQLDeleteQuery = new LiteQLDeleteQuery();
        liteQLDeleteQuery.setDomainType(domainType);

        return new LiteQLDeleteQueryConditionsBuilder(liteQLDeleteQuery);
    }

}
