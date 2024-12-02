package com.github.krystianmuchla.home.infrastructure.http.core.exception;

import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.id.SignInController;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class UnauthorizedException extends HttpException {
    @Override
    public void handleApi(HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 401);
    }

    @Override
    public void handleWeb(HttpExchange exchange) throws IOException {
        ResponseWriter.writeLocation(exchange, 302, SignInController.INSTANCE.getPath());
    }
}
