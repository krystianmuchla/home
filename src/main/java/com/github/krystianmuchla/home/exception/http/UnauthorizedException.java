package com.github.krystianmuchla.home.exception.http;

import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.id.user.UserGuardService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.UUID;

public class UnauthorizedException extends HttpException {
    private final UUID userId;

    public UnauthorizedException() {
        this(null);
    }

    public UnauthorizedException(final UUID userId) {
        this.userId = userId;
    }

    @Override
    public void handleApi(final HttpExchange exchange) throws IOException {
        if (userId != null) {
            UserGuardService.incrementAuthFailures(userId);
        }
        ResponseWriter.write(exchange, 401);
    }

    @Override
    public void handleWeb(final HttpExchange exchange) throws IOException {
        if (userId != null) {
            UserGuardService.incrementAuthFailures(userId);
        }
        ResponseWriter.writeLocation(exchange, 302, "/id/sign_in");
    }
}
