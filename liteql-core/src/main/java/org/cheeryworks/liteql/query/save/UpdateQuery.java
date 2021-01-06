package org.cheeryworks.liteql.query.save;

import org.cheeryworks.liteql.query.enums.QueryType;

public class UpdateQuery extends AbstractSaveQuery {

    @Override
    public QueryType getQueryType() {
        return QueryType.Update;
    }

}
