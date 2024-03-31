package com.github.krystianmuchla.home.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.krystianmuchla.home.error.exception.ContentTypeException;
import com.github.krystianmuchla.home.error.exception.InternalException;
import com.github.krystianmuchla.home.error.exception.RequestException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Function;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

public class RequestReader {
    public static <T> T readQueryParameter(
        final HttpServletRequest request,
        final String name,
        final Function<String, T> mapper
    ) {
        final var value = request.getParameter(name);
        if (value == null) {
            return null;
        }
        return mapper.apply(value);
    }

    public static <T> T readQueryParameter(final HttpServletRequest request,
                                           final String name,
                                           final Function<String, T> mapper,
                                           final T defaultValue) {
        final var value = readQueryParameter(request, name, mapper);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

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

    public static <T extends RequestBody> T readJson(final HttpServletRequest request, final Class<T> clazz) {
        final var contentType = request.getHeader("Content-Type");
        if (contentType != null && !contentType.contains("application/json")) {
            throw new ContentTypeException();
        }
        final BufferedReader reader;
        try {
            reader = request.getReader();
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
        final var requestBody = reader.lines().collect(joining(lineSeparator()));
        final var objectMapper = ObjectMapperHolder.INSTANCE;
        final T object;
        try {
            object = objectMapper.readValue(requestBody, clazz);
        } catch (final JsonProcessingException exception) {
            throw new RequestException(exception);
        }
        object.validate();
        return object;
    }

    public static Cookie[] readCookies(final HttpServletRequest request) {
        final var cookies = request.getCookies();
        if (cookies == null) {
            return new Cookie[]{};
        }
        return cookies;
    }
}
