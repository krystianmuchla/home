package com.github.krystianmuchla.home.infrastructure.http.exception;

import com.github.krystianmuchla.home.infrastructure.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Set;

import static com.github.krystianmuchla.home.application.html.Html.document;

public class NotFoundException extends HttpException {
    @Override
    public void handleApi(HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 404);
    }

    @Override
    public void handleWeb(HttpExchange exchange) throws IOException {
        var html = document(
            Set.of(),
            Set.of(),
            Set.of(),
            "Selected page doesn't exist."
        );
        ResponseWriter.writeHtml(exchange, 404, html);
    }
}
