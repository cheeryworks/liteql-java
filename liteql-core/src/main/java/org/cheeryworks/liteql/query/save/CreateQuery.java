package org.cheeryworks.liteql.query.save;

import org.cheeryworks.liteql.query.enums.QueryType;

public class CreateQuery extends AbstractSaveQuery {

    @Override
    public QueryType getQueryType() {
        return QueryType.Create;
    }

}
