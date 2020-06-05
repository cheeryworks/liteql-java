package org.cheeryworks.liteql.model.query.delete;

import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.query.AbstractConditionalQuery;
import org.cheeryworks.liteql.model.query.PublicQuery;
import org.cheeryworks.liteql.model.query.TypedQuery;

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
