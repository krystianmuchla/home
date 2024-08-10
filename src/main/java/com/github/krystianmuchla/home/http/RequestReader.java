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
    public static User readUser(HttpExchange exchange) {
        var user = exchange.getAttribute(Attribute.USER);
        if (user == null) {
            throw new InternalException("Could not find user attribute");
        }
        return (User) user;
    }

    public static <T extends RequestQuery> T readQuery(
        HttpExchange exchange,
        Function<MultiValueMap<String, String>, T> mapper
    ) {
        var query = readQuery(exchange);
        var object = mapper.apply(query);
        object.validate();
        return object;
    }

    @SuppressWarnings("unchecked")
    public static MultiValueMap<String, String> readQuery(HttpExchange exchange) {
        var query = exchange.getAttribute(Attribute.QUERY);
        if (query == null) {
            throw new InternalException("Could not find query attribute");
        }
        return (MultiValueMap<String, String>) query;
    }

    public static Map<String, String> readCookies(HttpExchange exchange) {
        var cookie = readHeader(exchange, "Cookie");
        return Cookie.parse(cookie);
    }

    public static String readHeader(HttpExchange exchange, String name) {
        return readHeaders(exchange).getFirst(name);
    }

    public static <T extends RequestHeaders> T readHeaders(HttpExchange exchange, Function<Headers, T> mapper) {
        var headers = readHeaders(exchange);
        var object = mapper.apply(headers);
        object.validate();
        return object;
    }

    public static Headers readHeaders(HttpExchange exchange) {
        return exchange.getRequestHeaders();
    }

    public static <T extends RequestBody> T readJson(HttpExchange exchange, Class<T> clazz) throws IOException {
        var contentType = readHeader(exchange, "Content-Type");
        if (contentType == null || !contentType.contains("application/json")) {
            throw new UnsupportedMediaTypeException();
        }
        String requestBody = readString(exchange);
        T object;
        try {
            object = GsonHolder.INSTANCE.fromJson(requestBody, clazz);
        } catch (JsonSyntaxException exception) {
            throw new BadRequestException(exception);
        }
        object.validate();
        return object;
    }

    public static InputStream readStream(HttpExchange exchange) {
        var contentType = readHeader(exchange, "Content-Type");
        if (contentType == null || !contentType.contains("application/octet-stream")) {
            throw new UnsupportedMediaTypeException();
        }
        return exchange.getRequestBody();
    }

    private static String readString(HttpExchange exchange) throws IOException {
        try (var inputStream = exchange.getRequestBody()) {
            return new String(inputStream.readAllBytes());
        }
    }
}
