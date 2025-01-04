package com.github.krystianmuchla.home.infrastructure.http.id;

import com.github.krystianmuchla.home.domain.id.error.UnauthenticatedException;
import com.github.krystianmuchla.home.domain.id.session.SessionService;
import com.github.krystianmuchla.home.domain.id.user.User;
import com.github.krystianmuchla.home.domain.id.user.UserService;
import com.github.krystianmuchla.home.infrastructure.http.core.Controller;
import com.github.krystianmuchla.home.infrastructure.http.core.Cookie;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestReader;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.core.error.BadRequestException;
import com.github.krystianmuchla.home.infrastructure.http.core.error.UnauthorizedException;
import com.github.krystianmuchla.home.infrastructure.http.core.error.ValidationError;
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
        var token = cookies.get("token");
        if (token == null) {
            throw new BadRequestException("Cookie", ValidationError.wrongFormat());
        }
        var result = SessionService.removeSession(token);
        if (result) {
            new ResponseWriter(exchange).status(204).write();
        } else {
            new ResponseWriter(exchange).status(410).write();
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
        }
        var token = SessionService.createSession(user);
        var cookie = Cookie.create("token", token);
        new ResponseWriter(exchange).status(204).cookies(cookie).write();
    }
}
