package com.github.krystianmuchla.home.infrastructure.http;

import com.github.krystianmuchla.home.infrastructure.http.exception.NotFoundException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpHandlerImpl implements HttpHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HttpHandlerImpl.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var path = getPath(exchange);
        var controller = Controllers.get(path);
        try {
            if (controller == null) {
                throw new NotFoundException();
            } else {
                controller.handle(exchange);
            }
        } catch (Exception exception) {
            LOG.warn("{}", exception.getMessage(), exception);
            HttpErrorHandler.handle(exchange, exception);
        }
    }

    private String getPath(HttpExchange exchange) {
        var uri = exchange.getRequestURI();
        return uri.getPath();
    }
}
