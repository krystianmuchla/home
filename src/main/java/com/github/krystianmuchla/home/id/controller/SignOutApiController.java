package com.github.krystianmuchla.home.id.controller;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.id.session.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SignOutApiController extends Controller {
    public static final String PATH = "/api/id/sign_out";

    @Override
    protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) {
        final var sessionId = sessionId(request);
        SessionService.removeSession(sessionId);
        response.setStatus(204);
    }
}
