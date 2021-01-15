package org.cheeryworks.liteql.skeleton.query.save;

import org.cheeryworks.liteql.skeleton.query.enums.QueryType;

public class UpdateQuery extends AbstractSaveQuery {

    @Override
    public QueryType getQueryType() {
        return QueryType.Update;
    }

}
