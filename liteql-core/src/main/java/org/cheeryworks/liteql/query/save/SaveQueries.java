package org.cheeryworks.liteql.query.save;

import org.cheeryworks.liteql.query.enums.QueryType;
import org.cheeryworks.liteql.query.PublicQuery;
import org.cheeryworks.liteql.query.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class SaveQueries extends ArrayList<AbstractSaveQuery> implements TypedQuery, PublicQuery {

    public SaveQueries() {
        super();
    }

    public SaveQueries(List<AbstractSaveQuery> saveQueries) {
        super(saveQueries);
    }

    @Override
    public QueryType getQueryType() {
        return QueryType.Save;
    }

}
