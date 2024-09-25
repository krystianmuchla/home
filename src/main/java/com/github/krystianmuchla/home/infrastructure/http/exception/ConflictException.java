package com.github.krystianmuchla.home.infrastructure.http.exception;

import com.github.krystianmuchla.home.infrastructure.http.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.api.ProblemResponseFactory;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.github.krystianmuchla.home.application.html.Html.document;

public class ConflictException extends HttpException {
    private final String reason;

    public ConflictException(String reason) {
        super("Reason: " + reason);
        this.reason = reason;
    }

    @Override
    public void handleApi(HttpExchange exchange) throws IOException {
        var response = ProblemResponseFactory.create(Map.of("reason", reason));
        ResponseWriter.writeProblemJson(exchange, 409, response);
    }

    @Override
    public void handleWeb(HttpExchange exchange) throws IOException {
        var html = document(
            Set.of(),
            Set.of(),
            Set.of(),
            "Something went wrong."
        );
        ResponseWriter.writeHtml(exchange, 409, html);
    }
}
