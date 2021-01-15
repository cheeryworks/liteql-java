package org.cheeryworks.liteql.skeleton.query.exception;

import org.cheeryworks.liteql.skeleton.exception.LiteQLException;
import org.cheeryworks.liteql.skeleton.query.PublicQuery;

public class UnsupportedQueryException extends LiteQLException {

    private PublicQuery query;

    public UnsupportedQueryException(PublicQuery query) {
        this.query = query;
    }

    public PublicQuery getQuery() {
        return query;
    }

}
