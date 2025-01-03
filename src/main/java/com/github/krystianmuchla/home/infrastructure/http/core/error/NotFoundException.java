package com.github.krystianmuchla.home.infrastructure.http.core.error;

import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class NotFoundException extends HttpException {
    @Override
    public void handleApi(HttpExchange exchange) throws IOException {
        new ResponseWriter(exchange).status(404).write();
    }

    @Override
    public void handleWeb(HttpExchange exchange) throws IOException {
        new ResponseWriter(exchange).status(404).text("Selected page doesn't exist.").write();
    }
}
