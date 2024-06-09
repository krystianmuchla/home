package com.github.krystianmuchla.home.exception;

import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class RequestException extends RuntimeException implements HttpException {
    public RequestException() {
    }

    public RequestException(final Throwable cause) {
        super(cause);
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 400);
    }
}
