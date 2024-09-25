package com.github.krystianmuchla.home.application.exception;

public class InternalException extends RuntimeException {
    public InternalException(String message) {
        super(message);
    }

    public InternalException(Throwable cause) {
        super(cause);
    }
}
