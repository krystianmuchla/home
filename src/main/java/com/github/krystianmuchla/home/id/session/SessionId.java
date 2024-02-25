package com.github.krystianmuchla.home.id.session;

import java.util.Objects;

import jakarta.servlet.http.Cookie;

public record SessionId(String login, String token) {
    public Cookie[] asCookies() {
        final var cookies = new Cookie[2];
        cookies[0] = SessionCookieFactory.create("login", login);
        cookies[1] = SessionCookieFactory.create("token", token);
        return cookies;
    }

    public static SessionId from(final Cookie[] cookies) {
        var cookie = findCookie(cookies, "login");
        if (cookie == null) {
            throw new IllegalArgumentException();
        }
        final var login = cookie.getValue();
        cookie = findCookie(cookies, "token");
        if (cookie == null) {
            throw new IllegalArgumentException();
        }
        final var token = cookie.getValue();
        return new SessionId(login, token);
    }

    private static Cookie findCookie(final Cookie[] cookies, final String name) {
        for (final var cookie : cookies) {
            if (Objects.equals(name, cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }
}
