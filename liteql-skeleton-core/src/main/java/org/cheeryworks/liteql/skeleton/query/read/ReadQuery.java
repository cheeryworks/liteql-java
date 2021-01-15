package org.cheeryworks.liteql.skeleton.query.read;

import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;
import org.cheeryworks.liteql.skeleton.query.read.result.ReadResults;

public class ReadQuery extends AbstractTypedReadQuery<ReadQuery, ReadResults> implements PublicQuery {

    public QueryType getQueryType() {
        return QueryType.Read;
    }

    public ReadResults getResult(ReadResults readResults) {
        return readResults;
    }

}
