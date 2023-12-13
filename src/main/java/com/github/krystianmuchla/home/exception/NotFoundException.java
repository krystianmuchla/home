package com.github.krystianmuchla.home.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ClientErrorException {
    public NotFoundException(final String message) {
        super(message);
        status = HttpStatus.NOT_FOUND;
    }
}
