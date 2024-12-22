package com.github.krystianmuchla.home.infrastructure.http.core.error;

import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HttpException extends RuntimeException {
    public HttpException() {
        super();
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public void handle(HttpExchange exchange) throws IOException {
        var path = exchange.getRequestURI().getPath();
        if (path.startsWith("/api/")) {
            handleApi(exchange);
        } else if (path.startsWith("/ui/")) {
            handleUi(exchange);
        } else {
            handleWeb(exchange);
        }
    }

    public void handleApi(HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 500);
    }

    public void handleUi(HttpExchange exchange) throws IOException {
        handleApi(exchange);
    }

    public void handleWeb(HttpExchange exchange) throws IOException {
        ResponseWriter.writeText(exchange, 500, "Something went wrong.");
    }
}
