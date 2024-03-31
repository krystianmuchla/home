package com.github.krystianmuchla.home.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.krystianmuchla.home.error.exception.InternalException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ResponseWriter {

    public static void writeJson(final HttpServletResponse response, final Object object) {
        final var objectMapper = ObjectMapperHolder.INSTANCE;
        final String string;
        try {
            string = objectMapper.writeValueAsString(object);
        } catch (final JsonProcessingException exception) {
            throw new InternalException(exception);
        }
        writeJson(response, string);
    }

    public static void writeJson(final HttpServletResponse response, final String string) {
        response.setContentType("application/json");
        final PrintWriter writer;
        try {
            writer = response.getWriter();
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
        writer.print(string);
    }

    public static void addCookies(final HttpServletResponse response, final Cookie[] cookies) {
        for (final var cookie : cookies) {
            response.addCookie(cookie);
        }
    }
}
