package org.cheeryworks.liteql.query.save;

import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.query.PublicQuery;
import org.cheeryworks.liteql.query.TypedQuery;

public class CreateQuery extends AbstractSaveQuery implements TypedQuery, PublicQuery {

    public QueryType getQueryType() {
        return QueryType.Create;
    }

}
