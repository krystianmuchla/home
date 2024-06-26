package com.github.krystianmuchla.home.exception.http;

import com.github.krystianmuchla.home.exception.ValidationError;
import com.github.krystianmuchla.home.exception.api.ProblemResponseFactory;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.util.MultiValueHashMap;
import com.github.krystianmuchla.home.util.MultiValueMap;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.github.krystianmuchla.home.html.Html.document;

public class BadRequestException extends HttpException {
    private final MultiValueMap<String, ValidationError> errors;

    public BadRequestException() {
        this.errors = null;
    }

    public BadRequestException(final Throwable cause) {
        super(cause);
        this.errors = null;
    }

    public BadRequestException(final String parameter, final ValidationError error) {
        this(MultiValueHashMap.of(parameter, error));
    }

    public BadRequestException(final MultiValueMap<String, ValidationError> errors) {
        super("Error keys: " + String.join(",", errors.keySet()));
        this.errors = errors;
    }

    @Override
    public void handleApi(final HttpExchange exchange) throws IOException {
        if (errors.isEmpty()) {
            ResponseWriter.write(exchange, 400);
        } else {
            final var response = ProblemResponseFactory.create(Map.of("errors", errors));
            ResponseWriter.writeJson(exchange, 400, response);
        }
    }

    @Override
    public void handleWeb(final HttpExchange exchange) throws IOException {
        final var html = document(
            Set.of(),
            Set.of(),
            Set.of(),
            "Something went wrong."
        );
        ResponseWriter.writeHtml(exchange, 400, html);
    }
}
