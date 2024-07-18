package com.github.krystianmuchla.home.controller;

import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class FaviconController extends Controller {
    public static final String PATH = "/favicon.ico";

    public FaviconController() {
        super(PATH);
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 200);
    }
}
