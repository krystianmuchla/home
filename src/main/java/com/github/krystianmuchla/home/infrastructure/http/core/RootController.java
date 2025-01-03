package com.github.krystianmuchla.home.infrastructure.http.core;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class RootController extends Controller {
    public static final RootController INSTANCE = new RootController();

    public RootController() {
        super("/");
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        new ResponseWriter(exchange).status(302).location(ControllerConfig.DEFAULT_PATH).write();
    }
}
