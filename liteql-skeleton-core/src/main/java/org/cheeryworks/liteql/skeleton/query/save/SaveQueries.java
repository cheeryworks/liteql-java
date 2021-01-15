package org.cheeryworks.liteql.skeleton.query.save;

import org.cheeryworks.liteql.skeleton.query.PublicQuery;
import org.cheeryworks.liteql.skeleton.query.TypedQuery;
import org.cheeryworks.liteql.skeleton.query.enums.QueryType;

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
