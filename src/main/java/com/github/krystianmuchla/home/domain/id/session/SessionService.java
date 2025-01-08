package com.github.krystianmuchla.home.domain.id.session;

import com.github.krystianmuchla.home.domain.id.SecureRandomFactory;
import com.github.krystianmuchla.home.domain.id.error.UnauthenticatedException;
import com.github.krystianmuchla.home.domain.id.user.User;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionService {
    public static final SessionService INSTANCE = new SessionService();
    private static final int TOKEN_BYTES = 32;

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public String createSession(User user) {
        var token = generateToken();
        var session = new Session(user);
        sessions.put(token, session);
        return token;
    }

    public Session getSession(String token) throws UnauthenticatedException {
        var session = sessions.get(token);
        if (session == null) {
            throw new UnauthenticatedException();
        }
        return session;
    }

    public boolean removeSession(String token) {
        var session = sessions.remove(token);
        return session != null;
    }

    private static String generateToken() {
        var token = SecureRandomFactory.createBytes(TOKEN_BYTES);
        return Base64.getEncoder().encodeToString(token);
    }
}
