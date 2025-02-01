package com.github.krystianmuchla.home.infrastructure.http.core;

import com.github.krystianmuchla.home.infrastructure.http.core.error.MethodNotAllowedException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Set;

public abstract class Controller {
    public final Set<String> paths;

    protected Controller(String path) {
        paths = Set.of(path);
    }

    protected Controller(Set<String> paths) {
        this.paths = paths;
    }

    public String getPath() {
        if (paths.size() != 1) {
            throw new UnsupportedOperationException();
        }
        return paths.iterator().next();
    }

    public void handle(HttpExchange exchange) throws IOException {
        var method = Method.of(exchange.getRequestMethod());
        switch (method) {
            case DELETE -> delete(exchange);
            case GET -> get(exchange);
            case PATCH -> patch(exchange);
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

    protected void patch(HttpExchange exchange) throws IOException {
        throw new MethodNotAllowedException();
    }

    protected void post(HttpExchange exchange) throws IOException {
        throw new MethodNotAllowedException();
    }

    protected void put(HttpExchange exchange) throws IOException {
        throw new MethodNotAllowedException();
    }
}
