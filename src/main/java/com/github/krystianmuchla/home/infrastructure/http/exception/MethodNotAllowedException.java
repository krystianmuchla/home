package com.github.krystianmuchla.home.infrastructure.http.exception;

import com.github.krystianmuchla.home.infrastructure.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Set;

import static com.github.krystianmuchla.home.application.html.Html.document;

public class MethodNotAllowedException extends HttpException {
    @Override
    public void handleApi(HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 405);
    }

    @Override
    public void handleWeb(HttpExchange exchange) throws IOException {
        var response = document(
            Set.of(),
            Set.of(),
            Set.of(),
            "Something went wrong."
        );
        ResponseWriter.writeHtml(exchange, 405, response);
    }
}
