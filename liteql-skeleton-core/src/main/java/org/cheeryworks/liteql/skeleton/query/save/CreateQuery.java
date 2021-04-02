package org.cheeryworks.liteql.skeleton.query.save;

import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.TypedQuery;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;

public class CreateQuery extends AbstractSaveQuery implements TypedQuery, PublicQuery {

    @Override
    public QueryType getQueryType() {
        return QueryType.Create;
    }

}
