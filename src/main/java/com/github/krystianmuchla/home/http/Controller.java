package com.github.krystianmuchla.home.http;

import com.github.krystianmuchla.home.exception.http.MethodNotAllowedException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public abstract class Controller {
    public final List<String> segments;

    protected Controller(String path) {
        segments = Segment.segments(path);
    }

    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "DELETE" -> delete(exchange);
            case "GET" -> get(exchange);
            case "POST" -> post(exchange);
            case "PUT" -> put(exchange);
            default -> throw new MethodNotAllowedException();
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

