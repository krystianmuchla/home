package com.github.krystianmuchla.home.error.exception;

import com.github.krystianmuchla.home.error.AppError;
import jakarta.servlet.http.HttpServletResponse;

public class InternalException extends RuntimeException implements AppError {
    public InternalException(final String message) {
        super(message);
    }

    public InternalException(final Throwable cause) {
        super(cause);
    }

    @Override
    public void handle(HttpServletResponse response) {
        response.setStatus(500);
    }
}
