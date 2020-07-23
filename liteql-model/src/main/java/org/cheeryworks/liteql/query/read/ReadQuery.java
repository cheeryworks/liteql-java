package org.cheeryworks.liteql.query.read;

import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.query.PublicQuery;

public class ReadQuery extends AbstractTypedReadQuery<ReadQuery> implements PublicQuery {

    public QueryType getQueryType() {
        return QueryType.Read;
    }

}
