package com.github.krystianmuchla.home.infrastructure.http.id;

import com.github.krystianmuchla.home.domain.id.SignUpToken;
import com.github.krystianmuchla.home.domain.id.session.SessionService;
import com.github.krystianmuchla.home.domain.id.user.User;
import com.github.krystianmuchla.home.domain.id.user.UserService;
import com.github.krystianmuchla.home.domain.id.user.exception.UserAlreadyExistsException;
import com.github.krystianmuchla.home.domain.id.user.exception.UserNotFoundException;
import com.github.krystianmuchla.home.infrastructure.http.core.Controller;
import com.github.krystianmuchla.home.infrastructure.http.core.Cookie;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestReader;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.core.exception.ConflictException;
import com.github.krystianmuchla.home.infrastructure.http.core.exception.InternalServerErrorException;
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
        var userId = Transaction.run(() -> {
            try {
                return UserService.create(signUpRequest.name(), signUpRequest.login(), signUpRequest.password());
            } catch (UserAlreadyExistsException exception) {
                throw new ConflictException();
            }
        });
        User user;
        try {
            user = UserService.get(userId);
        } catch (UserNotFoundException exception) {
            throw new InternalServerErrorException();
        }
        var sessionId = SessionService.createSession(signUpRequest.login(), user);
        ResponseWriter.writeCookies(exchange, 201, Cookie.fromSessionId(sessionId));
    }
}
