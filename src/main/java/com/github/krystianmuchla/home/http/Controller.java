package com.github.krystianmuchla.home.http;

import com.github.krystianmuchla.home.exception.MethodNotAllowedException;
import com.github.krystianmuchla.home.id.session.Session;
import com.github.krystianmuchla.home.id.session.SessionService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    protected Session session(final HttpExchange exchange) {
        final var sessionId = RequestReader.readSessionId(exchange);
        return SessionService.getSession(sessionId);
    }

    public static Controller find(Map<String, Route> routes, final String path) {
        final var segments = Segment.segments(path);
        for (var index = 0; index < segments.size(); index++) {
            final var last = index == segments.size() - 1;
            final var segment = segments.get(index);
            final var route = routes.get(segment);
            if (route == null) {
                return null;
            }
            if (last) {
                return route.controller;
            } else {
                routes = route.children;
            }
        }
        return null;
    }
}
