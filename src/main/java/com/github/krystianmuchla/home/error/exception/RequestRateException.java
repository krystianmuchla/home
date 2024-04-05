package com.github.krystianmuchla.home.error.exception;

import com.github.krystianmuchla.home.error.AppError;
import jakarta.servlet.http.HttpServletResponse;

public class RequestRateException extends RuntimeException implements AppError {
    public RequestRateException() {
        super("Too many requests");
    }

    @Override
    public void handle(final HttpServletResponse response) {
        response.setStatus(429);
    }
}
