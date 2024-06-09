package com.github.krystianmuchla.home.exception.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface HttpException {
    void handle(final HttpExchange exchange) throws IOException;
}
