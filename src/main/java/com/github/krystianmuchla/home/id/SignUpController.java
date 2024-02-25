package com.github.krystianmuchla.home.id;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.id.session.SessionManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

public class SignUpController extends Controller {
    public static final String PATH = "/api/id/sign_up";

    private final IdService idService = IdService.INSTANCE;

    @Override
    @SneakyThrows
    protected void doPost(final HttpServletRequest request, HttpServletResponse response) {
        final var signUpRequest = RequestReader.readJson(request, SignUpRequest.class);
        final var userId = Transaction.run(() -> idService.createUser(signUpRequest.login(), signUpRequest.password()));
        final var cookies = SessionManager.createSession(signUpRequest.login(), userId);
        ResponseWriter.addCookies(response, cookies);
        response.setStatus(201);
    }
}
