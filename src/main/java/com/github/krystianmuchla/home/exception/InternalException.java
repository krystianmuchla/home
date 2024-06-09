package com.github.krystianmuchla.home.exception;

public class InternalException extends RuntimeException {
    public InternalException(final String message) {
        super(message);
    }

    public InternalException(final Throwable cause) {
        super(cause);
    }
}
