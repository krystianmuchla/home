package com.github.krystianmuchla.home.infrastructure.http.core.error;

import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.id.SignInController;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class UnauthorizedException extends HttpException {
    @Override
    public void handleApi(HttpExchange exchange) throws IOException {
        new ResponseWriter(exchange).status(401).write();
    }

    @Override
    public void handleWeb(HttpExchange exchange) throws IOException {
        new ResponseWriter(exchange).status(302).location(SignInController.INSTANCE.getPath()).write();
    }
}
