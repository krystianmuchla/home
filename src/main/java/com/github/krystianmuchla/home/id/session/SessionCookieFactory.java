package com.github.krystianmuchla.home.id.session;

import jakarta.servlet.http.Cookie;

public class SessionCookieFactory {
    public static Cookie create(final String name, final String value) {
        final var cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
