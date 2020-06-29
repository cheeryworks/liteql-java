package org.cheeryworks.liteql.util.builder.delete;

import org.cheeryworks.liteql.model.query.delete.DeleteQuery;

public class LiteQLDeleteQueryBuilder {

    protected DeleteQuery getQuery(LiteQLDeleteQuery liteQLDeleteQuery) {
        DeleteQuery deleteQuery = new DeleteQuery();

        deleteQuery.setDomainTypeName(liteQLDeleteQuery.getDomainType());
        deleteQuery.setTruncated(liteQLDeleteQuery.isTruncated());
        deleteQuery.setConditions(liteQLDeleteQuery.getConditions());

        return deleteQuery;
    }

}
