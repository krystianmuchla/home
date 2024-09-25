package com.github.krystianmuchla.home.domain.id.session;

import com.github.krystianmuchla.home.application.exception.ValidationError;
import com.github.krystianmuchla.home.domain.id.api.SignInRequest;
import com.github.krystianmuchla.home.domain.id.user.UserService;
import com.github.krystianmuchla.home.infrastructure.http.Controller;
import com.github.krystianmuchla.home.infrastructure.http.RequestReader;
import com.github.krystianmuchla.home.infrastructure.http.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.exception.BadRequestException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class SessionApiController extends Controller {
    public static final String PATH = "/api/sessions";

    public SessionApiController() {
        super(PATH);
    }

    @Override
    protected void delete(HttpExchange exchange) throws IOException {
        var cookies = RequestReader.readCookies(exchange);
        var sessionId = SessionId.fromCookies(cookies);
        if (sessionId.isEmpty()) {
            throw new BadRequestException("Cookie", ValidationError.wrongFormat());
        }
        var result = SessionService.removeSession(sessionId.get());
        if (result) {
            ResponseWriter.write(exchange, 204);
        } else {
            ResponseWriter.write(exchange, 410);
        }
    }

    @Override
    protected void post(HttpExchange exchange) throws IOException {
        var signInRequest = RequestReader.readJson(exchange, SignInRequest.class);
        var user = UserService.get(signInRequest.login(), signInRequest.password());
        var sessionId = SessionService.createSession(signInRequest.login(), user);
        ResponseWriter.writeCookies(exchange, 204, sessionId.asCookies());
    }
}
