package com.github.krystianmuchla.home.id.controller;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.id.SignUpToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InitSignUpApiController extends Controller {
    public static final String PATH = "/api/id/sign_up/init";

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) {
        final var success = SignUpToken.INSTANCE.generateAndLog();
        if (success) {
            response.setStatus(202);
        } else {
            response.setStatus(409);
        }
    }
}
