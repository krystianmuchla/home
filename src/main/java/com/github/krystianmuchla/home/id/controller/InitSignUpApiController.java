package com.github.krystianmuchla.home.id.controller;

import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.id.SignUpToken;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class InitSignUpApiController extends Controller {
    public static final String PATH = "/api/id/sign_up/init";

    public InitSignUpApiController() {
        super(PATH);
    }

    @Override
    protected void post(final HttpExchange exchange) throws IOException {
        final var success = SignUpToken.INSTANCE.generateAndLog();
        if (success) {
            ResponseWriter.write(exchange, 202);
        } else {
            ResponseWriter.write(exchange, 409);
        }
    }
}
