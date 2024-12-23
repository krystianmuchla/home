package com.github.krystianmuchla.home.infrastructure.http.id;

import com.github.krystianmuchla.home.infrastructure.http.core.error.ValidationError;
import com.github.krystianmuchla.home.domain.id.error.UnauthenticatedException;
import com.github.krystianmuchla.home.domain.id.session.SessionId;
import com.github.krystianmuchla.home.domain.id.session.SessionService;
import com.github.krystianmuchla.home.domain.id.session.error.SessionValidationException;
import com.github.krystianmuchla.home.domain.id.user.User;
import com.github.krystianmuchla.home.domain.id.user.UserService;
import com.github.krystianmuchla.home.domain.id.user.error.UserBlockedException;
import com.github.krystianmuchla.home.infrastructure.http.core.Controller;
import com.github.krystianmuchla.home.infrastructure.http.core.Cookie;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestReader;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.core.error.BadRequestException;
import com.github.krystianmuchla.home.infrastructure.http.core.error.InternalServerErrorException;
import com.github.krystianmuchla.home.infrastructure.http.core.error.TooManyRequestsException;
import com.github.krystianmuchla.home.infrastructure.http.core.error.UnauthorizedException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class SessionApiController extends Controller {
    public static final SessionApiController INSTANCE = new SessionApiController();

    public SessionApiController() {
        super("/api/sessions");
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
        User user;
        try {
            user = UserService.get(signInRequest.login(), signInRequest.password());
        } catch (UnauthenticatedException exception) {
            throw new UnauthorizedException();
        } catch (UserBlockedException exception) {
            throw new TooManyRequestsException();
        }
        SessionId sessionId;
        try {
            sessionId = SessionService.createSession(signInRequest.login(), user);
        } catch (SessionValidationException exception) {
            throw new InternalServerErrorException(exception);
        }
        ResponseWriter.writeCookies(exchange, 204, Cookie.fromSessionId(sessionId));
    }
}
