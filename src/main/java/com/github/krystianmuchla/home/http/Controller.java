package com.github.krystianmuchla.home.http;

import com.github.krystianmuchla.home.exception.http.MethodNotAllowedException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public abstract class Controller {
    public final List<String> segments;

    protected Controller(final String path) {
        segments = Segment.segments(path);
    }

    public void handle(final HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "DELETE" -> delete(exchange);
            case "GET" -> get(exchange);
            case "POST" -> post(exchange);
            case "PUT" -> put(exchange);
            default -> throw new MethodNotAllowedException();
        }
    }

    protected void delete(final HttpExchange exchange) throws IOException {
        throw new MethodNotAllowedException();
    }

    protected void get(final HttpExchange exchange) throws IOException {
        throw new MethodNotAllowedException();
    }

    protected void post(final HttpExchange exchange) throws IOException {
        throw new MethodNotAllowedException();
    }

    protected void put(final HttpExchange exchange) throws IOException {
        throw new MethodNotAllowedException();
    }
}
