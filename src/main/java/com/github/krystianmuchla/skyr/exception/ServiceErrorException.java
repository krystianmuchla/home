package com.github.krystianmuchla.skyr.exception;

public class ServiceErrorException extends RuntimeException {
    public ServiceErrorException(final String message) {
        super(message);
    }

    public ServiceErrorException(final Throwable cause) {
        super(cause);
    }
}
