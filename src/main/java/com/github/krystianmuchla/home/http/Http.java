package com.github.krystianmuchla.home.http;

import com.github.krystianmuchla.home.exception.HttpException;
import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.exception.TransactionException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;

public class Http implements HttpHandler {
    private static final Logger LOG = LoggerFactory.getLogger(Http.class);
    private final Map<String, Route> routes = Route.routes(HttpConfig.CONTROLLERS);

    public static void startServer(final int port) {
        final HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
        final var context = server.createContext("/", new Http());
        context.getFilters().add(new QueryFilter());
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
        LOG.info("Http server started at port {}", port);
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        final var uri = exchange.getRequestURI();
        final var controller = Controller.find(routes, uri.getPath());
        if (controller == null) {
            ResponseWriter.write(exchange, 404);
            return;
        }
        try {
            controller.handle(exchange);
        } catch (final Exception exception) {
            handle(exchange, exception);
            LOG.warn("{}", exception.getMessage(), exception);
        }
    }

    private void handle(final HttpExchange exchange, final Throwable throwable) throws IOException {
        switch (throwable) {
            case HttpException httpException -> httpException.handle(exchange);
            case TransactionException transactionException -> {
                final var cause = transactionException.getCause();
                if (cause == null) {
                    ResponseWriter.write(exchange, 500);
                } else {
                    handle(exchange, cause);
                }
            }
            default -> ResponseWriter.write(exchange, 500);
        }
    }
}
