package com.github.krystianmuchla.home.exception;

import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class MissingResourceException extends RuntimeException implements HttpException {
    public MissingResourceException() {
        super("Not found");
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 404);
    }
}
