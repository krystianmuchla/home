package com.github.krystianmuchla.home.id.controller;

import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.RequestReader;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.id.session.SessionService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class SignOutApiController extends Controller {
    public SignOutApiController() {
        super("/api/id/sign_out");
    }

    @Override
    protected void delete(final HttpExchange exchange) throws IOException {
        final var sessionId = RequestReader.readSessionId(exchange);
        SessionService.removeSession(sessionId);
        ResponseWriter.write(exchange, 204);
    }
}
