package org.cheeryworks.liteql.model.query.exception;

import org.cheeryworks.liteql.model.query.PublicQuery;

public class UnsupportedQueryException extends RuntimeException {

    private PublicQuery query;

    public UnsupportedQueryException(PublicQuery query) {
        this.query = query;
    }

    public PublicQuery getQuery() {
        return query;
    }

}
