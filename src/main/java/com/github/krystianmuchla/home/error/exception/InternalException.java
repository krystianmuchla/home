package com.github.krystianmuchla.home.error.exception;

import com.github.krystianmuchla.home.error.AppError;
import jakarta.servlet.http.HttpServletResponse;

public class InternalException extends RuntimeException implements AppError {
    public InternalException(final String message) {
        super(message);
    }

    @Override
    public void accept(final HttpServletResponse response) {
        response.setStatus(500);
    }
}
