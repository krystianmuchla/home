package com.github.krystianmuchla.home.exception;

import org.springframework.http.HttpStatus;

public class ClientErrorException extends RuntimeException {
    protected HttpStatus status;

    public ClientErrorException(final String message) {
        super(message);
        status = null;
    }

    public HttpStatus getStatus() {
        if (status == null) return HttpStatus.BAD_REQUEST;
        return status;
    }
}
