package com.github.krystianmuchla.home.infrastructure.http.api;

import com.github.krystianmuchla.home.infrastructure.http.Controller;
import com.github.krystianmuchla.home.infrastructure.http.HttpConfig;
import com.github.krystianmuchla.home.infrastructure.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class RootController extends Controller {
    public static final String PATH = "/";

    public RootController() {
        super(PATH);
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        ResponseWriter.writeLocation(exchange, 302, HttpConfig.DEFAULT_PATH);
    }
}
