package com.github.krystianmuchla.home;

import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class FaviconController extends Controller {
    public FaviconController() {
        super("/favicon.ico");
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        ResponseWriter.write(exchange, 200);
    }
}
