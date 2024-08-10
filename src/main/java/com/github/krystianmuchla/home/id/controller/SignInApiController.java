package com.github.krystianmuchla.home.id.controller;

import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.RequestReader;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.id.SignInRequest;
import com.github.krystianmuchla.home.id.session.SessionService;
import com.github.krystianmuchla.home.id.user.UserService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class SignInApiController extends Controller {
    public static final String PATH = "/api/id/sign_in";

    public SignInApiController() {
        super(PATH);
    }

    @Override
    protected void post(HttpExchange exchange) throws IOException {
        var signInRequest = RequestReader.readJson(exchange, SignInRequest.class);
        var user = UserService.getUser(signInRequest.login(), signInRequest.password());
        var sessionId = SessionService.createSession(signInRequest.login(), user);
        ResponseWriter.writeCookies(exchange, 204, sessionId.asCookies());
    }
}
