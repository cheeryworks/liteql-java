package org.cheeryworks.liteql.query.delete;

import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.query.AbstractConditionalQuery;
import org.cheeryworks.liteql.query.PublicQuery;
import org.cheeryworks.liteql.query.TypedQuery;

public class DeleteQuery extends AbstractConditionalQuery implements TypedQuery, PublicQuery {

    private boolean truncated;

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public QueryType getQueryType() {
        return QueryType.Delete;
    }

}
