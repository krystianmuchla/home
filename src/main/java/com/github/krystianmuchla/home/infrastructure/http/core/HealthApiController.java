package com.github.krystianmuchla.home.infrastructure.http.core;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HealthApiController extends Controller {
    public static final HealthApiController INSTANCE = new HealthApiController();

    public HealthApiController() {
        super("/api/health");
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        new ResponseWriter(exchange).json("{}").write();
    }
}
