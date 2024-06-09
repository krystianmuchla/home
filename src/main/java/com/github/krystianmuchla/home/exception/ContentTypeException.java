package com.github.krystianmuchla.home.exception;

import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ContentTypeException extends RuntimeException implements HttpException {
    public ContentTypeException() {
        super("Unsupported content type");
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 415);
    }
}
