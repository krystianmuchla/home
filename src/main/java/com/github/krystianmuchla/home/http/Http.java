package com.github.krystianmuchla.home.http;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.http.filter.AuthFilter;
import com.github.krystianmuchla.home.http.filter.QueryFilter;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

public class Http {
    private static final Logger LOG = LoggerFactory.getLogger(Http.class);

    public static void startServer(final int port) {
        final HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
        final var context = server.createContext("/", new HttpHandlerImpl());
        context.getFilters().addAll(filters());
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
        LOG.info("Http server started at port {}", port);
    }

    private static List<Filter> filters() {
        return List.of(new AuthFilter(), new QueryFilter());
    }
}
