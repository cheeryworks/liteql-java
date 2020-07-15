package org.cheeryworks.liteql.model.util.builder.query.delete;

import org.cheeryworks.liteql.model.query.delete.DeleteQuery;

public class DeleteQueryBuilder {

    protected DeleteQuery getQuery(DeleteQueryMetadata deleteQueryMetadata) {
        DeleteQuery deleteQuery = new DeleteQuery();

        deleteQuery.setDomainTypeName(deleteQueryMetadata.getDomainTypeName());
        deleteQuery.setTruncated(deleteQueryMetadata.isTruncated());
        deleteQuery.setConditions(deleteQueryMetadata.getConditions());

        return deleteQuery;
    }

}
