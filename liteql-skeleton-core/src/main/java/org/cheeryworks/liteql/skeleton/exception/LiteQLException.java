package org.cheeryworks.liteql.skeleton.exception;

public class LiteQLException extends RuntimeException {

    public LiteQLException() {
        super();
    }

    public LiteQLException(String message) {
        super(message);
    }

    public LiteQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public LiteQLException(Throwable cause) {
        super(cause);
    }

}
