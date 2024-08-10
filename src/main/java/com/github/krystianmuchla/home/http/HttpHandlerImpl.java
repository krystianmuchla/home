package com.github.krystianmuchla.home.http;

import com.github.krystianmuchla.home.exception.http.NotFoundException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpHandlerImpl implements HttpHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HttpHandlerImpl.class);

    private final Routes routes = new Routes(HttpConfig.CONTROLLERS);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var uri = exchange.getRequestURI();
        var controller = routes.findController(uri.getPath());
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
}
