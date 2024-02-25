package com.github.krystianmuchla.home.id;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.id.session.SessionManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SignOutController extends Controller {
    public static final String PATH = "/api/id/sign_out";

    @Override
    protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) {
        final var cookies = RequestReader.readCookies(request);
        SessionManager.removeSession(cookies);
        response.setStatus(204);
    }
}
