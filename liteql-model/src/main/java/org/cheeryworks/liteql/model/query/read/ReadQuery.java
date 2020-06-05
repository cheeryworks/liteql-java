package org.cheeryworks.liteql.model.query.read;

import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.query.PublicQuery;

public class ReadQuery extends AbstractTypedReadQuery<ReadQuery> implements PublicQuery {

    public QueryType getQueryType() {
        return QueryType.Read;
    }

}
