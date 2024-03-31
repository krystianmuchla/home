package com.github.krystianmuchla.home.id;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.error.exception.AuthenticationException;
import com.github.krystianmuchla.home.id.session.SessionService;
import com.github.krystianmuchla.home.id.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SignUpController extends Controller {
    public static final String PATH = "/api/id/sign_up";

    @Override
    protected void doPost(final HttpServletRequest request, HttpServletResponse response) {
        final var signUpRequest = RequestReader.readJson(request, SignUpRequest.class);
        final var tokenValid = SignUpToken.INSTANCE.test(signUpRequest.token());
        if (!tokenValid) {
            throw new AuthenticationException();
        }
        final var userId = Transaction.run(() -> UserService.createUser(signUpRequest.login(), signUpRequest.password()));
        final var sessionId = SessionService.createSession(signUpRequest.login(), userId);
        ResponseWriter.addCookies(response, sessionId.asCookies());
        response.setStatus(201);
    }
}
