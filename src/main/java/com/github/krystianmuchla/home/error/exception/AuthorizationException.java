package com.github.krystianmuchla.home.error.exception;

import com.github.krystianmuchla.home.error.AppError;
import jakarta.servlet.http.HttpServletResponse;

public class AuthorizationException extends RuntimeException implements AppError {
    public AuthorizationException() {
        super("Unauthorized");
    }

    @Override
    public void accept(final HttpServletResponse response) {
        response.setStatus(401);
    }
}
