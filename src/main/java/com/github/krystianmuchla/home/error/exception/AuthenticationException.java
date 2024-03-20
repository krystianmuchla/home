package com.github.krystianmuchla.home.error.exception;

import com.github.krystianmuchla.home.error.AppError;
import com.github.krystianmuchla.home.id.user.UserAuthService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

public class AuthenticationException extends RuntimeException implements AppError {
    private final UUID userId;

    public AuthenticationException() {
        this(null);
    }

    public AuthenticationException(final UUID userId) {
        super("Unauthenticated");
        this.userId = userId;
    }

    @Override
    public void handle(final HttpServletResponse response) {
        if (userId != null) {
            UserAuthService.incrementAuthFailures(userId);
        }
        response.setStatus(401);
    }
}
