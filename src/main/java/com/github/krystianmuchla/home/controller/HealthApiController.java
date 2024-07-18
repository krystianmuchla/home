package com.github.krystianmuchla.home.controller;

import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HealthApiController extends Controller {
    public static final String PATH = "/api/health";

    public HealthApiController() {
        super(PATH);
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        ResponseWriter.writeJson(exchange, 200, "{}");
    }
}
