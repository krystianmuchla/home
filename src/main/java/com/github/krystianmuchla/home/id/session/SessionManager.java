package com.github.krystianmuchla.home.id.session;

import com.github.krystianmuchla.home.id.SecureRandomFactory;
import com.github.krystianmuchla.home.id.user.User;
import jakarta.servlet.http.Cookie;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final int TOKEN_BYTES;
    private static final Map<SessionId, SessionData> SESSIONS;

    static {
        TOKEN_BYTES = 32;
        SESSIONS = Collections.synchronizedMap(new HashMap<>());
    }

    public static Cookie[] createSession(final String login, final User user) {
        final var sessionId = new SessionId(login, generateToken());
        final var sessionData = new SessionData(user);
        SESSIONS.put(sessionId, sessionData);
        return sessionId.asCookies();
    }

    public static SessionData getSession(final Cookie[] cookies) {
        final SessionId sessionId;
        try {
            sessionId = SessionId.from(cookies);
        } catch (final Exception exception) {
            return null;
        }
        return SESSIONS.get(sessionId);
    }

    public static void removeSession(final Cookie[] cookies) {
        final SessionId sessionId = SessionId.from(cookies);
        SESSIONS.remove(sessionId);
    }

    private static String generateToken() {
        final var token = SecureRandomFactory.create(TOKEN_BYTES);
        return Base64.getEncoder().encodeToString(token);
    }
}
