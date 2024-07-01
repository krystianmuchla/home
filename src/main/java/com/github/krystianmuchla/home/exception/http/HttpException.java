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

    public HttpException(final String message) {
        super(message);
    }

    public HttpException(final Throwable throwable) {
        super(throwable);
    }

    public void handle(final HttpExchange exchange) throws IOException {
        final var path = exchange.getRequestURI().getPath();
        if (path.startsWith("/api/")) {
            handleApi(exchange);
        } else if (path.startsWith("/ui/")) {
            handleUi(exchange);
        } else {
            handleWeb(exchange);
        }
    }

    public void handleApi(final HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 500);
    }

    public void handleUi(final HttpExchange exchange) throws IOException {
        handleApi(exchange);
    }

    public void handleWeb(final HttpExchange exchange) throws IOException {
        final var html = document(
            Set.of(),
            Set.of(),
            Set.of(),
            "Something went wrong."
        );
        ResponseWriter.writeHtml(exchange, 500, html);
    }
}
