package com.github.krystianmuchla.home.id;

import com.github.krystianmuchla.home.api.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitSignUpController extends Controller {
    public static final String PATH = "/api/id/sign_up/init";

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) {
        final var success = SignUpToken.INSTANCE.createAndLog();
        if (success) {
            response.setStatus(202);
        } else {
            response.setStatus(409);
        }
    }
}
