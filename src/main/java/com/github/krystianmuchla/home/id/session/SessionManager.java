package com.github.krystianmuchla.home.id.session;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.krystianmuchla.home.id.SecureRandomFactory;
import com.github.krystianmuchla.home.id.user.User;

import jakarta.servlet.http.Cookie;

public class SessionManager {
    private static final int TOKEN_BYTES;
    private static final Map<SessionId, SessionData> SESSIONS;
    private static final Encoder ENCODER;

    static {
        TOKEN_BYTES = 32;
        SESSIONS = Collections.synchronizedMap(new HashMap<>());
        ENCODER = Base64.getEncoder();
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
        } catch (final IllegalArgumentException exception) {
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
        return ENCODER.encodeToString(token);
    }
}
