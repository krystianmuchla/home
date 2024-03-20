package com.github.krystianmuchla.home.id.session;

import com.github.krystianmuchla.home.error.exception.AuthenticationException;
import jakarta.servlet.http.Cookie;

import java.util.Objects;

public record SessionId(String login, String token) {
    public Cookie[] asCookies() {
        final var cookies = new Cookie[2];
        cookies[0] = SessionCookieFactory.create("login", login);
        cookies[1] = SessionCookieFactory.create("token", token);
        return cookies;
    }

    public static SessionId from(final Cookie[] cookies) {
        final var login = findCookie(cookies, "login");
        if (login == null) {
            throw new AuthenticationException();
        }
        final var token = findCookie(cookies, "token");
        if (token == null) {
            throw new AuthenticationException();
        }
        return new SessionId(login.getValue(), token.getValue());
    }

    private static Cookie findCookie(final Cookie[] cookies, final String name) {
        if (cookies == null) {
            return null;
        }
        for (final var cookie : cookies) {
            if (Objects.equals(name, cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }
}
