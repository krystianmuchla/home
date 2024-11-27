package com.github.krystianmuchla.home.domain.id.user;

import com.github.krystianmuchla.home.domain.id.SignUpToken;
import com.github.krystianmuchla.home.domain.id.api.SignUpRequest;
import com.github.krystianmuchla.home.domain.id.session.SessionService;
import com.github.krystianmuchla.home.infrastructure.http.core.Controller;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestReader;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.core.exception.UnauthorizedException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Transaction;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class UserApiController extends Controller {
    public static final UserApiController INSTANCE = new UserApiController();

    public UserApiController() {
        super("/api/users");
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
