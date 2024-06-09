package com.github.krystianmuchla.home.exception.http;

import com.github.krystianmuchla.home.exception.ValidationError;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.util.MultiValueHashMap;
import com.github.krystianmuchla.home.util.MultiValueMap;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

public class BadRequestException extends RuntimeException implements HttpException {
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
        super("Invalid input parameters: " + String.join(",", errors.keySet()));
        this.errors = errors;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (errors.isEmpty()) {
            ResponseWriter.write(exchange, 400);
        } else {
            ResponseWriter.writeJson(exchange, 400, Map.of("invalidParameters", errors));
        }
    }
}
