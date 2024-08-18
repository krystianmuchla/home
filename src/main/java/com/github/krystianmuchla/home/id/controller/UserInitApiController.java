package com.github.krystianmuchla.home.id.controller;

import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.id.SignUpToken;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class UserInitApiController extends Controller {
    public static final String PATH = "/api/users/init";

    public UserInitApiController() {
        super(PATH);
    }

    @Override
    protected void post(HttpExchange exchange) throws IOException {
        var success = SignUpToken.INSTANCE.generateAndLog();
        if (success) {
            ResponseWriter.write(exchange, 202);
        } else {
            ResponseWriter.write(exchange, 409);
        }
    }
}
