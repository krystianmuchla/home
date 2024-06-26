package com.github.krystianmuchla.home.exception.http;

import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Set;

import static com.github.krystianmuchla.home.html.Html.document;

public class TooManyRequestsException extends HttpException {
    @Override
    public void handleApi(final HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 429);
    }

    @Override
    public void handleWeb(final HttpExchange exchange) throws IOException {
        final var html = document(
            Set.of(),
            Set.of(),
            Set.of(),
            "Try again later."
        );
        ResponseWriter.writeHtml(exchange, 429, html);
    }
}
