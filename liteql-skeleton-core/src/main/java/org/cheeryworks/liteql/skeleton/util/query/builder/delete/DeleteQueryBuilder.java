package org.cheeryworks.liteql.skeleton.util.query.builder.delete;

import org.cheeryworks.liteql.skeleton.query.delete.DeleteQuery;

public class DeleteQueryBuilder {

    protected DeleteQuery getQuery(DeleteQueryMetadata deleteQueryMetadata) {
        DeleteQuery deleteQuery = new DeleteQuery();

        deleteQuery.setDomainTypeName(deleteQueryMetadata.getDomainTypeName());
        deleteQuery.setTruncated(deleteQueryMetadata.isTruncated());
        deleteQuery.setConditions(deleteQueryMetadata.getConditions());

        return deleteQuery;
    }

}
