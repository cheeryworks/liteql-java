package org.cheeryworks.liteql.model.query.save;

import org.cheeryworks.liteql.model.enums.QueryType;
import org.cheeryworks.liteql.model.query.PublicQuery;
import org.cheeryworks.liteql.model.query.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class SaveQueries extends ArrayList<SaveQuery> implements TypedQuery, PublicQuery {

    public SaveQueries() {
        super();
    }

    public SaveQueries(List<SaveQuery> saveQueries) {
        super(saveQueries);
    }

    @Override
    public QueryType getQueryType() {
        return QueryType.Save;
    }

}
