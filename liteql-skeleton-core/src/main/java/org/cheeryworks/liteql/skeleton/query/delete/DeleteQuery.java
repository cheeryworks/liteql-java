package org.cheeryworks.liteql.skeleton.query.delete;

import org.cheeryworks.liteql.skeleton.query.AbstractConditionalQuery;
import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.TypedQuery;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;

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
