package com.github.krystianmuchla.home.exception;

import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

public class ConflictException extends RuntimeException implements HttpException {
    private final String error;

    public ConflictException(final String error) {
        super("Conflict error: " + error);
        this.error = error;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        ResponseWriter.writeJson(exchange, 409, Map.of("error", error));
    }
}
