package com.github.krystianmuchla.home.infrastructure.http.core.error;

import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class MethodNotAllowedException extends HttpException {
    @Override
    public void handleApi(HttpExchange exchange) throws IOException {
        new ResponseWriter(exchange).status(405).write();
    }

    @Override
    public void handleWeb(HttpExchange exchange) throws IOException {
        new ResponseWriter(exchange).status(405).text("Something went wrong.").write();
    }
}
