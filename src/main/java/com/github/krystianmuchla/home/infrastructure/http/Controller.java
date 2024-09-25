package com.github.krystianmuchla.home.infrastructure.http;

import com.github.krystianmuchla.home.infrastructure.http.exception.MethodNotAllowedException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public abstract class Controller {
    public final List<String> segments;

    protected Controller(String path) {
        segments = Segment.segments(path);
    }

    public void handle(HttpExchange exchange) throws IOException {
        var method = Method.of(exchange.getRequestMethod());
        switch (method) {
            case DELETE -> delete(exchange);
            case GET -> get(exchange);
            case POST -> post(exchange);
            case PUT -> put(exchange);
        }
    }

    protected void delete(HttpExchange exchange) throws IOException {
        throw new MethodNotAllowedException();
    }

    protected void get(HttpExchange exchange) throws IOException {
        throw new MethodNotAllowedException();
    }

    protected void post(HttpExchange exchange) throws IOException {
        throw new MethodNotAllowedException();
    }

    protected void put(HttpExchange exchange) throws IOException {
        throw new MethodNotAllowedException();
    }
}

