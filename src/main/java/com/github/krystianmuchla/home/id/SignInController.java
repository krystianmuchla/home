package com.github.krystianmuchla.home.id;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.id.session.SessionManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

public class SignInController extends Controller {
    public static final String PATH = "/api/id/sign_in";

    private final IdService idService = IdService.INSTANCE;

    @Override
    @SneakyThrows
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) {
        final var signInRequest = RequestReader.readJson(request, SignInRequest.class);
        final var user = idService.getUser(signInRequest.login(), signInRequest.password());
        final var cookies = SessionManager.createSession(signInRequest.login(), user);
        ResponseWriter.addCookies(response, cookies);
        response.setStatus(204);
    }
}
