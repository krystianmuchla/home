package com.github.krystianmuchla.home.exception.http;

import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class TooManyRequestsException extends RuntimeException implements HttpException {
    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 429);
    }
}
