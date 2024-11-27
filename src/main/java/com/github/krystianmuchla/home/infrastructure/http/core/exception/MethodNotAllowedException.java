package com.github.krystianmuchla.home.infrastructure.http.core.exception;

import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class MethodNotAllowedException extends HttpException {
    @Override
    public void handleApi(HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 405);
    }

    @Override
    public void handleWeb(HttpExchange exchange) throws IOException {
        ResponseWriter.writeText(exchange, 405, "Something went wrong.");
    }
}
