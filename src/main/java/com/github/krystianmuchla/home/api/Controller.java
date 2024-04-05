package com.github.krystianmuchla.home.api;

import com.github.krystianmuchla.home.error.exception.AuthenticationException;
import com.github.krystianmuchla.home.id.accessdata.AccessDataSql;
import com.github.krystianmuchla.home.id.session.Session;
import com.github.krystianmuchla.home.id.session.SessionId;
import com.github.krystianmuchla.home.id.session.SessionService;
import com.github.krystianmuchla.home.id.user.UserGuardService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

public abstract class Controller extends HttpServlet {
    protected SessionId sessionId(final HttpServletRequest request) {
        final var cookies = RequestReader.readCookies(request);
        return SessionId.from(cookies);
    }

    protected Session session(final HttpServletRequest request) {
        final var sessionId = sessionId(request);
        final var accessData = AccessDataSql.readByLogin(sessionId.login());
        if (accessData == null) {
            throw new AuthenticationException();
        }
        UserGuardService.inspect(accessData.userId());
        final var session = SessionService.getSession(sessionId);
        if (session == null) {
            throw new AuthenticationException(accessData.userId());
        }
        return session;
    }
}
