package com.github.krystianmuchla.home.http;

import com.github.krystianmuchla.home.api.GsonHolder;
import com.github.krystianmuchla.home.api.RequestBody;
import com.github.krystianmuchla.home.api.RequestHeaders;
import com.github.krystianmuchla.home.api.RequestQuery;
import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.exception.http.UnsupportedMediaTypeException;
import com.github.krystianmuchla.home.id.user.User;
import com.github.krystianmuchla.home.util.MultiValueMap;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;

public class RequestReader {
    public static User readUser(final HttpExchange exchange) {
        final var user = exchange.getAttribute(Attribute.USER);
        if (user == null) {
            throw new InternalException("Could not find user attribute");
        }
        return (User) user;
    }

    public static <T extends RequestQuery> T readQuery(
        final HttpExchange exchange,
        final Function<MultiValueMap<String, String>, T> mapper
    ) {
        final var query = readQuery(exchange);
        final var object = mapper.apply(query);
        object.validate();
        return object;
    }

    @SuppressWarnings("unchecked")
    public static MultiValueMap<String, String> readQuery(final HttpExchange exchange) {
        final var query = exchange.getAttribute(Attribute.QUERY);
        if (query == null) {
            throw new InternalException("Could not find query attribute");
        }
        return (MultiValueMap<String, String>) query;
    }

    public static Map<String, String> readCookies(final HttpExchange exchange) {
        final var cookie = readHeader(exchange, "Cookie");
        return Cookie.parse(cookie);
    }

    public static String readHeader(final HttpExchange exchange, final String name) {
        return readHeaders(exchange).getFirst(name);
    }

    public static <T extends RequestHeaders> T readHeaders(
        final HttpExchange exchange,
        final Function<Headers, T> mapper
    ) {
        final var headers = readHeaders(exchange);
        final var object = mapper.apply(headers);
        object.validate();
        return object;
    }

    public static Headers readHeaders(final HttpExchange exchange) {
        return exchange.getRequestHeaders();
    }

    public static <T extends RequestBody> T readJson(
        final HttpExchange exchange,
        final Class<T> clazz
    ) throws IOException {
        final var contentType = readHeader(exchange, "Content-Type");
        if (contentType == null || !contentType.contains("application/json")) {
            throw new UnsupportedMediaTypeException();
        }
        final String requestBody = readString(exchange);
        final T object;
        try {
            object = GsonHolder.INSTANCE.fromJson(requestBody, clazz);
        } catch (final JsonSyntaxException exception) {
            throw new BadRequestException(exception);
        }
        object.validate();
        return object;
    }

    public static InputStream readStream(final HttpExchange exchange) {
        final var contentType = readHeader(exchange, "Content-Type");
        if (contentType == null || !contentType.contains("application/octet-stream")) {
            throw new UnsupportedMediaTypeException();
        }
        return exchange.getRequestBody();
    }

    private static String readString(final HttpExchange exchange) throws IOException {
        try (final var inputStream = exchange.getRequestBody()) {
            return new String(inputStream.readAllBytes());
        }
    }
}
