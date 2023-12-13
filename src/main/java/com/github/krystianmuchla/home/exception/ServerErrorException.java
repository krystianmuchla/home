package com.github.krystianmuchla.home.exception;

import org.springframework.http.HttpStatus;

public class ServerErrorException extends RuntimeException {
    private final HttpStatus status;

    public ServerErrorException(final String message) {
        super(message);
        this.status = null;
    }

    public ServerErrorException(final Throwable cause) {
        super(cause);
        this.status = null;
    }

    public HttpStatus getStatus() {
        if (status == null) return HttpStatus.INTERNAL_SERVER_ERROR;
        return status;
    }
}
