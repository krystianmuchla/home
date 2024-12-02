package com.github.krystianmuchla.home.application.exception;

// todo delete
public class InternalException extends RuntimeException {
    public InternalException(String message) {
        super(message);
    }

    public InternalException(Throwable cause) {
        super(cause);
    }
}
