package com.github.krystianmuchla.home.exception.validation;

import com.github.krystianmuchla.home.exception.HttpException;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.util.MultiValueHashMap;
import com.github.krystianmuchla.home.util.MultiValueMap;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

public class ValidationException extends RuntimeException implements HttpException {
    private final MultiValueMap<String, ValidationError> errors;

    public ValidationException() {
        errors = new MultiValueHashMap<>();
    }

    public ValidationException(final String parameter, ValidationError error) {
        this(MultiValueHashMap.of(parameter, error));
    }

    public ValidationException(final MultiValueMap<String, ValidationError> errors) {
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
