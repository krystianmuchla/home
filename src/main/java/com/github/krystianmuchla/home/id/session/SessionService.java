package com.github.krystianmuchla.home.id.session;

import com.github.krystianmuchla.home.id.SecureRandomFactory;
import com.github.krystianmuchla.home.id.user.User;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionService {
    private static final int TOKEN_BYTES;
    private static final Map<SessionId, Session> SESSIONS;

    static {
        TOKEN_BYTES = 32;
        SESSIONS = new ConcurrentHashMap<>();
    }

    public static SessionId createSession(final String login, final User user) {
        final var sessionId = new SessionId(login, generateToken());
        final var session = new Session(user);
        SESSIONS.put(sessionId, session);
        return sessionId;
    }

    public static Session getSession(final SessionId sessionId) {
        if (sessionId == null) {
            return null;
        }
        return SESSIONS.get(sessionId);
    }

    public static void removeSession(final SessionId sessionId) {
        SESSIONS.remove(sessionId);
    }

    private static String generateToken() {
        final var token = SecureRandomFactory.createBytes(TOKEN_BYTES);
        return Base64.getEncoder().encodeToString(token);
    }
}
