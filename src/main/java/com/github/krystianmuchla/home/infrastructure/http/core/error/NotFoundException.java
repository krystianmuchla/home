package com.github.krystianmuchla.home.infrastructure.http.core.error;

import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class NotFoundException extends HttpException {
    @Override
    public void handleApi(HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 404);
    }

    @Override
    public void handleWeb(HttpExchange exchange) throws IOException {
        ResponseWriter.writeText(exchange, 404, "Selected page doesn't exist.");
    }
}
