package com.github.krystianmuchla.home.infrastructure.http.core.error;

import com.github.krystianmuchla.home.application.util.MultiValueHashMap;
import com.github.krystianmuchla.home.application.util.MultiValueMap;
import com.github.krystianmuchla.home.infrastructure.http.core.ProblemResponseFactory;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

public class BadRequestException extends HttpException {
    private final MultiValueMap<String, ValidationError> errors;

    public BadRequestException() {
        this.errors = null;
    }

    public BadRequestException(Throwable cause) {
        super(cause);
        this.errors = null;
    }

    public BadRequestException(String parameter, ValidationError error) {
        this(MultiValueHashMap.of(parameter, error));
    }

    public BadRequestException(MultiValueMap<String, ValidationError> errors) {
        super("Error keys: " + String.join(",", errors.keySet()));
        this.errors = errors;
    }

    @Override
    public void handleApi(HttpExchange exchange) throws IOException {
        if (errors == null || errors.isEmpty()) {
            new ResponseWriter(exchange).status(400).write();
        } else {
            var response = ProblemResponseFactory.create(Map.of("errors", errors));
            new ResponseWriter(exchange).status(400).json(response).write();
        }
    }

    @Override
    public void handleWeb(HttpExchange exchange) throws IOException {
        new ResponseWriter(exchange).status(400).text("Something went wrong.").write();
    }
}
