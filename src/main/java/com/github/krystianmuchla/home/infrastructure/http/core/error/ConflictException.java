package com.github.krystianmuchla.home.infrastructure.http.core.error;

import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ConflictException extends HttpException {
    @Override
    public void handleApi(HttpExchange exchange) throws IOException {
        new ResponseWriter(exchange).status(409).write();
    }

    @Override
    public void handleWeb(HttpExchange exchange) throws IOException {
        new ResponseWriter(exchange).status(409).text("Something went wrong.").write();
    }
}
