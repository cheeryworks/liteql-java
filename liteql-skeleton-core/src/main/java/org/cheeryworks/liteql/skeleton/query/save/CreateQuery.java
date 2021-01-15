package org.cheeryworks.liteql.skeleton.query.save;

import org.cheeryworks.liteql.skeleton.query.enums.QueryType;

public class CreateQuery extends AbstractSaveQuery {

    @Override
    public QueryType getQueryType() {
        return QueryType.Create;
    }

}
