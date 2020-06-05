package org.cheeryworks.liteql.model.query.save;

import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.query.PublicQuery;
import org.cheeryworks.liteql.model.query.TypedQuery;

public class CreateQuery extends SaveQuery implements TypedQuery, PublicQuery {

    public QueryType getQueryType() {
        return QueryType.Create;
    }

}
