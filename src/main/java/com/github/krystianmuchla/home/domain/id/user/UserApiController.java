package com.github.krystianmuchla.home.domain.id.user;

import com.github.krystianmuchla.home.domain.id.SignUpToken;
import com.github.krystianmuchla.home.domain.id.api.SignUpRequest;
import com.github.krystianmuchla.home.domain.id.session.SessionService;
import com.github.krystianmuchla.home.infrastructure.http.Controller;
import com.github.krystianmuchla.home.infrastructure.http.RequestReader;
import com.github.krystianmuchla.home.infrastructure.http.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.exception.UnauthorizedException;
import com.github.krystianmuchla.home.infrastructure.persistence.Transaction;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class UserApiController extends Controller {
    public static final String PATH = "/api/users";

    public UserApiController() {
        super(PATH);
    }

    @Override
    protected void post(HttpExchange exchange) throws IOException {
        var signUpRequest = RequestReader.readJson(exchange, SignUpRequest.class);
        var tokenValid = SignUpToken.INSTANCE.test(signUpRequest.token());
        if (!tokenValid) {
            throw new UnauthorizedException();
        }
        var userId = Transaction.run(
            () -> UserService.create(signUpRequest.name(), signUpRequest.login(), signUpRequest.password())
        );
        var sessionId = SessionService.createSession(signUpRequest.login(), UserService.get(userId));
        ResponseWriter.writeCookies(exchange, 201, sessionId.asCookies());
    }
}
