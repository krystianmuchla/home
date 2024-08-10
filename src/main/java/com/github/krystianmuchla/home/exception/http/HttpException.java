package com.github.krystianmuchla.home.exception.http;

import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Set;

import static com.github.krystianmuchla.home.html.Html.document;

public class HttpException extends RuntimeException {
    public HttpException() {
        super();
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(Throwable throwable) {
        super(throwable);
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
        var html = document(
            Set.of(),
            Set.of(),
            Set.of(),
            "Something went wrong."
        );
        ResponseWriter.writeHtml(exchange, 500, html);
    }
}
