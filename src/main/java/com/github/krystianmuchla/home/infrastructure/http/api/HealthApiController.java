package com.github.krystianmuchla.home.infrastructure.http.api;

import com.github.krystianmuchla.home.infrastructure.http.Controller;
import com.github.krystianmuchla.home.infrastructure.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HealthApiController extends Controller {
    public static final HealthApiController INSTANCE = new HealthApiController();

    public HealthApiController() {
        super("/api/health");
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        ResponseWriter.writeJson(exchange, 200, "{}");
    }
}
