package com.github.krystianmuchla.home.domain.id.session;

import com.github.krystianmuchla.home.domain.id.SecureRandomFactory;
import com.github.krystianmuchla.home.domain.id.error.UnauthenticatedException;
import com.github.krystianmuchla.home.domain.id.session.error.SessionValidationException;
import com.github.krystianmuchla.home.domain.id.user.User;
import com.github.krystianmuchla.home.domain.id.user.UserGuardService;
import com.github.krystianmuchla.home.domain.id.user.error.UserBlockedException;
import com.github.krystianmuchla.home.infrastructure.persistence.id.AccessDataPersistence;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionService {
    private static final int TOKEN_BYTES = 32;
    private static final Map<SessionId, Session> SESSIONS = new ConcurrentHashMap<>();

    public static SessionId createSession(String login, User user) throws SessionValidationException {
        var sessionId = new SessionId(login, generateToken());
        var session = new Session(user);
        SESSIONS.put(sessionId, session);
        return sessionId;
    }

    public static Session getSession(SessionId sessionId) throws UnauthenticatedException, UserBlockedException {
        var accessData = AccessDataPersistence.read(sessionId.login());
        if (accessData == null) {
            throw new UnauthenticatedException();
        }
        UserGuardService.inspect(accessData.userId);
        var session = SESSIONS.get(sessionId);
        if (session == null) {
            UserGuardService.incrementAuthFailures(accessData.userId);
            throw new UnauthenticatedException();
        }
        return session;
    }

    public static boolean removeSession(SessionId sessionId) {
        var session = SESSIONS.remove(sessionId);
        return session != null;
    }

    private static String generateToken() {
        var token = SecureRandomFactory.createBytes(TOKEN_BYTES);
        return Base64.getEncoder().encodeToString(token);
    }
}
