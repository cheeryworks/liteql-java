package org.cheeryworks.liteql.query.read;

import org.cheeryworks.liteql.query.PublicQuery;
import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.query.read.result.ReadResults;

public class ReadQuery extends AbstractTypedReadQuery<ReadQuery, ReadResults> implements PublicQuery {

    public QueryType getQueryType() {
        return QueryType.Read;
    }

    public ReadResults getResult(ReadResults readResults) {
        return readResults;
    }

}
