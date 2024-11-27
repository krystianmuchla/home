package com.github.krystianmuchla.home.infrastructure.http.core;

import com.github.krystianmuchla.home.application.exception.InternalException;
import com.github.krystianmuchla.home.infrastructure.http.core.filter.AuthFilter;
import com.github.krystianmuchla.home.infrastructure.http.core.filter.QueryFilter;
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

    public static void startServer(int port) {
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException exception) {
            throw new InternalException(exception);
        }
        var context = server.createContext("/", new HttpHandlerImpl());
        context.getFilters().addAll(filters());
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
        LOG.info("Http server started at port {}", port);
    }

    private static List<Filter> filters() {
        return List.of(new AuthFilter(), new QueryFilter());
    }
}
