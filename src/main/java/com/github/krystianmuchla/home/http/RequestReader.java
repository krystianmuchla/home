package com.github.krystianmuchla.home.http;

import com.github.krystianmuchla.home.api.GsonHolder;
import com.github.krystianmuchla.home.api.RequestBody;
import com.github.krystianmuchla.home.api.RequestQuery;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.exception.http.UnsupportedMediaTypeException;
import com.github.krystianmuchla.home.id.session.SessionId;
import com.github.krystianmuchla.home.util.MultiValueHashMap;
import com.github.krystianmuchla.home.util.MultiValueMap;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;

public class RequestReader {
    private static final Logger LOG = LoggerFactory.getLogger(RequestReader.class);

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
        final var query = exchange.getAttribute("query");
        if (query == null) {
            LOG.warn("Could not find query attribute");
            return new MultiValueHashMap<>();
        }
        return (MultiValueMap<String, String>) query;
    }

    public static SessionId readSessionId(final HttpExchange exchange) {
        final var cookies = readCookies(exchange);
        return new SessionId(cookies.get("login"), cookies.get("token"));
    }

    public static Map<String, String> readCookies(final HttpExchange exchange) {
        final var cookie = readHeader(exchange, "Cookie");
        return Cookie.parse(cookie);
    }

    public static String readHeader(final HttpExchange exchange, final String name) {
        return headers(exchange).getFirst(name);
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

    private static Headers headers(final HttpExchange exchange) {
        return exchange.getRequestHeaders();
    }
}
