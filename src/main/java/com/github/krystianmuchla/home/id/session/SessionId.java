package com.github.krystianmuchla.home.id.session;

import com.github.krystianmuchla.home.exception.AuthenticationException;
import com.github.krystianmuchla.home.http.Cookie;

import java.util.ArrayList;
import java.util.List;

public record SessionId(String login, String token) {
    public SessionId {
        if (login == null || token == null) {
            throw new AuthenticationException();
        }
    }

    public List<String> asCookies() {
        final var cookies = new ArrayList<String>();
        cookies.add(Cookie.create("login", login));
        cookies.add(Cookie.create("token", token));
        return cookies;
    }
}
