package com.github.krystianmuchla.home.infrastructure.http.core.exception;

import com.github.krystianmuchla.home.domain.id.api.SignInController;
import com.github.krystianmuchla.home.domain.id.user.UserGuardService;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.UUID;

public class UnauthorizedException extends HttpException {
    private final UUID userId;

    public UnauthorizedException() {
        this(null);
    }

    public UnauthorizedException(UUID userId) {
        this.userId = userId;
    }

    @Override
    public void handleApi(HttpExchange exchange) throws IOException {
        if (userId != null) {
            UserGuardService.incrementAuthFailures(userId);
        }
        ResponseWriter.write(exchange, 401);
    }

    @Override
    public void handleWeb(HttpExchange exchange) throws IOException {
        if (userId != null) {
            UserGuardService.incrementAuthFailures(userId);
        }
        ResponseWriter.writeLocation(exchange, 302, SignInController.INSTANCE.getPath());
    }
}
