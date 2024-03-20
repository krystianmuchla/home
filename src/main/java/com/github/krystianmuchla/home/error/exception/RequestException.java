package com.github.krystianmuchla.home.error.exception;

import com.github.krystianmuchla.home.error.AppError;
import jakarta.servlet.http.HttpServletResponse;

public class RequestException extends RuntimeException implements AppError {
    public RequestException(final Throwable cause) {
        super(cause);
    }

    @Override
    public void handle(HttpServletResponse response) {
        response.setStatus(400);
    }
}
