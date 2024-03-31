package com.github.krystianmuchla.home.id;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.id.session.SessionService;
import com.github.krystianmuchla.home.id.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SignInController extends Controller {
    public static final String PATH = "/api/id/sign_in";

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) {
        final var signInRequest = RequestReader.readJson(request, SignInRequest.class);
        final var user = UserService.getUser(signInRequest.login(), signInRequest.password());
        final var sessionId = SessionService.createSession(signInRequest.login(), user);
        ResponseWriter.addCookies(response, sessionId.asCookies());
        response.setStatus(204);
    }
}
