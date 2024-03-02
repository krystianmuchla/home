package com.github.krystianmuchla.home.api;

import com.github.krystianmuchla.home.error.exception.AuthorizationException;
import com.github.krystianmuchla.home.id.session.SessionData;
import com.github.krystianmuchla.home.id.session.SessionManager;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

public abstract class Controller extends HttpServlet {
    protected SessionData sessionData(final HttpServletRequest request) {
        final var cookies = RequestReader.readCookies(request);
        final var sessionData = SessionManager.getSession(cookies);
        if (sessionData == null) {
            throw new AuthorizationException();
        }
        return sessionData;
    }
}
