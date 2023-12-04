package com.github.krystianmuchla.skyr.exception;

public class ClientErrorException extends RuntimeException {
    public ClientErrorException(final String message) {
        super(message);
    }
}
