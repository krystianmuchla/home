package com.github.krystianmuchla.home.id.controller;

import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.exception.http.UnauthorizedException;
import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.RequestReader;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.id.SignUpRequest;
import com.github.krystianmuchla.home.id.SignUpToken;
import com.github.krystianmuchla.home.id.session.SessionService;
import com.github.krystianmuchla.home.id.user.UserService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class SignUpApiController extends Controller {
    public SignUpApiController() {
        super("/api/id/sign_up");
    }

    @Override
    protected void post(final HttpExchange exchange) throws IOException {
        final var signUpRequest = RequestReader.readJson(exchange, SignUpRequest.class);
        final var tokenValid = SignUpToken.INSTANCE.test(signUpRequest.token());
        if (!tokenValid) {
            throw new UnauthorizedException();
        }
        final var userId = Transaction.run(
            () -> UserService.createUser(signUpRequest.name(), signUpRequest.login(), signUpRequest.password())
        );
        final var sessionId = SessionService.createSession(signUpRequest.login(), userId);
        ResponseWriter.writeCookies(exchange, 201, sessionId.asCookies());
    }
}
