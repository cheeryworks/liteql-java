package org.cheeryworks.liteql.query.exception;

import org.cheeryworks.liteql.query.PublicQuery;

public class UnsupportedQueryException extends RuntimeException {

    private PublicQuery query;

    public UnsupportedQueryException(PublicQuery query) {
        this.query = query;
    }

    public PublicQuery getQuery() {
        return query;
    }

}
