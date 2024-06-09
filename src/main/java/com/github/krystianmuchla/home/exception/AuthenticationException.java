package com.github.krystianmuchla.home.exception;

import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.id.user.UserGuardService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.UUID;

public class AuthenticationException extends RuntimeException implements HttpException {
    private final UUID userId;

    public AuthenticationException() {
        this(null);
    }

    public AuthenticationException(final UUID userId) {
        super("Unauthenticated");
        this.userId = userId;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (userId != null) {
            UserGuardService.incrementAuthFailures(userId);
        }
        ResponseWriter.write(exchange, 401);
    }
}
