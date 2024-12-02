package com.github.krystianmuchla.home.domain.id.session;

import java.util.Map;
import java.util.Optional;

public record SessionId(String login, String token) {
    public SessionId {
        if (login == null) {
            throw new IllegalArgumentException();
        }
        if (token == null) {
            throw new IllegalArgumentException();
        }
    }

    public static Optional<SessionId> fromCookies(Map<String, String> cookies) {
        var login = cookies.get("login");
        if (login == null) {
            return Optional.empty();
        }
        var token = cookies.get("token");
        if (token == null) {
            return Optional.empty();
        }
        return Optional.of(new SessionId(login, token));
    }
}
