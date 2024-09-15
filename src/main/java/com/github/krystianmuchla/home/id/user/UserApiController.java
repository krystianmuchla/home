package com.github.krystianmuchla.home.id.user;

import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.exception.http.UnauthorizedException;
import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.RequestReader;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.id.SignUpRequest;
import com.github.krystianmuchla.home.id.SignUpToken;
import com.github.krystianmuchla.home.id.session.SessionService;
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
