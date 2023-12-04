package com.example.skyr.exception;

public class ServiceErrorException extends RuntimeException {
    public ServiceErrorException(final String message) {
        super(message);
    }

    public ServiceErrorException(final Throwable cause) {
        super(cause);
    }
}
