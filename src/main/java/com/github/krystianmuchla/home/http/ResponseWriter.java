package com.github.krystianmuchla.home.http;

import com.github.krystianmuchla.home.api.GsonHolder;
import com.github.krystianmuchla.home.util.StreamService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ResponseWriter {
    public static void write(final HttpExchange exchange, final int status) throws IOException {
        exchange.sendResponseHeaders(status, ResponseLength.NONE);
        exchange.getResponseBody().close();
    }

    public static void writeCookies(final HttpExchange exchange, final List<String> cookies) {
        final var headers = headers(exchange);
        cookies.forEach(cookie -> headers.add("Set-Cookie", cookie));
    }

    public static void writeLocation(final HttpExchange exchange, final String location) {
        headers(exchange).add("Location", location);
    }

    public static void writeHtml(final HttpExchange exchange, final int status, final String html) throws IOException {
        headers(exchange).add("Content-Type", "text/html");
        final var bytes = html.getBytes();
        exchange.sendResponseHeaders(status, bytes.length);
        writeBytes(exchange, bytes);
    }

    public static void writeJson(
        final HttpExchange exchange,
        final int status,
        final Object object
    ) throws IOException {
        final var json = GsonHolder.INSTANCE.toJson(object);
        writeJson(exchange, status, json);
    }

    public static void writeJson(
        final HttpExchange exchange,
        final int status,
        final String string
    ) throws IOException {
        headers(exchange).add("Content-Type", "application/json");
        final var bytes = string.getBytes();
        exchange.sendResponseHeaders(status, bytes.length);
        writeBytes(exchange, bytes);
    }

    public static void writeStream(
        final HttpExchange exchange,
        final int status,
        final InputStream inputStream
    ) throws IOException {
        headers(exchange).add("Content-Type", "application/octet-stream");
        exchange.sendResponseHeaders(status, ResponseLength.CHUNKED);
        try (final var outputStream = exchange.getResponseBody()) {
            StreamService.copy(inputStream, outputStream);
        }
    }

    private static void writeBytes(final HttpExchange exchange, final byte[] bytes) throws IOException {
        try (final var outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
            outputStream.flush();
        }
    }

    private static Headers headers(final HttpExchange exchange) {
        return exchange.getResponseHeaders();
    }
}
