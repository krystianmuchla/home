package com.github.krystianmuchla.home.api;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.util.function.Function;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestReader {

    public static String readPathParameter(final HttpServletRequest request) {
        final var pathParameter = request.getPathInfo();
        if (pathParameter == null) {
            return null;
        }
        if (pathParameter.startsWith("/")) {
            return pathParameter.substring(1);
        }
        return pathParameter;
    }

    public static <T> T readPathParameter(final HttpServletRequest request, final Function<String, T> mapper) {
        final var pathParameter = readPathParameter(request);
        if (pathParameter == null) {
            return null;
        }
        return mapper.apply(pathParameter);
    }

    public static <T extends RequestBody> T readJson(
            final HttpServletRequest request,
            final Class<T> clazz) throws IOException {
        final var contentType = request.getHeader("Content-Type");
        if (contentType != null && !contentType.contains("application/json")) {
            throw new IllegalArgumentException();
        }
        final var requestBody = request.getReader().lines().collect(joining(lineSeparator()));
        final var objectMapper = ObjectMapperHolder.INSTANCE;
        final var object = objectMapper.readValue(requestBody, clazz);
        object.validate();
        return object;
    }

    public static Cookie[] readCookies(final HttpServletRequest request) {
        final var cookies = request.getCookies();
        if (cookies == null) {
            return new Cookie[] {};
        }
        return cookies;
    }
}
