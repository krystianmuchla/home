package com.github.krystianmuchla.home.domain.id.session;

import com.github.krystianmuchla.home.application.exception.InternalException;
import com.github.krystianmuchla.home.infrastructure.http.Cookie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record SessionId(String login, String token) {
    public SessionId {
        if (login == null) {
            throw new InternalException("Login cannot be null");
        }
        if (token == null) {
            throw new InternalException("Token cannot be null");
        }
    }

    public List<String> asCookies() {
        var cookies = new ArrayList<String>();
        cookies.add(Cookie.create("login", login));
        cookies.add(Cookie.create("token", token));
        return cookies;
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
