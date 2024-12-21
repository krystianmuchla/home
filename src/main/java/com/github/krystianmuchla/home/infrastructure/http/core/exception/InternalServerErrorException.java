package com.github.krystianmuchla.home.infrastructure.http.core.exception;

public class InternalServerErrorException extends HttpException {
    public InternalServerErrorException() {
        super();
    }

    public InternalServerErrorException(Throwable cause) {
        super(cause);
    }
}
