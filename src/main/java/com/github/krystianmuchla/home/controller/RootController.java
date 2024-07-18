package com.github.krystianmuchla.home.controller;

import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.HttpConfig;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class RootController extends Controller {
    public RootController() {
        super("/");
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        ResponseWriter.writeLocation(exchange, 302, HttpConfig.DEFAULT_PATH);
    }
}
