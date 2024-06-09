package com.github.krystianmuchla.home.exception;

import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class BadRequestException extends RuntimeException implements HttpException {
    public BadRequestException(final Throwable cause) {
        super(cause);
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 400);
    }
}
