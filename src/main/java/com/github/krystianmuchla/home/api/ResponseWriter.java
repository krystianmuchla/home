package com.github.krystianmuchla.home.api;

import java.io.IOException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseWriter {

    public static void writeJson(final HttpServletResponse response, final Object object) throws IOException {
        final var objectMapper = ObjectMapperHolder.INSTANCE;
        writeJson(response, objectMapper.writeValueAsString(object));
    }

    public static void writeJson(final HttpServletResponse response, final String string) throws IOException {
        response.setContentType("application/json");
        response.getWriter().print(string);
    }

    public static void addCookies(final HttpServletResponse response, final Cookie[] cookies) {
        for (final var cookie : cookies) {
            response.addCookie(cookie);
        }
    }
}
