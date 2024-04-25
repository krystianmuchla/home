package com.github.krystianmuchla.home.error.exception;

import com.github.krystianmuchla.home.error.AppError;
import jakarta.servlet.http.HttpServletResponse;

public class AuthorizationException extends RuntimeException implements AppError {
    @Override
    public void handle(final HttpServletResponse response) {
        response.setStatus(403);
    }
}
