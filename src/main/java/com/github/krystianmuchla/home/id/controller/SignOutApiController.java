package com.github.krystianmuchla.home.id.controller;

import com.github.krystianmuchla.home.exception.ValidationError;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.RequestReader;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.id.session.SessionId;
import com.github.krystianmuchla.home.id.session.SessionService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class SignOutApiController extends Controller {
    public SignOutApiController() {
        super("/api/id/sign_out");
    }

    @Override
    protected void delete(final HttpExchange exchange) throws IOException {
        final var cookies = RequestReader.readCookies(exchange);
        final var sessionId = SessionId.fromCookies(cookies);
        if (sessionId.isEmpty()) {
            throw new BadRequestException("Cookie", ValidationError.wrongFormat());
        }
        final var result = SessionService.removeSession(sessionId.get());
        if (result) {
            ResponseWriter.write(exchange, 204);
        } else {
            ResponseWriter.write(exchange, 410);
        }
    }
}
