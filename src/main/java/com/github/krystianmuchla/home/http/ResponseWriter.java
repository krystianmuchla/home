package com.github.krystianmuchla.home.http;

import com.github.krystianmuchla.home.api.GsonHolder;
import com.github.krystianmuchla.home.util.StreamService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ResponseWriter {
    public static void write(final HttpExchange exchange, final int status) throws IOException {
        exchange.sendResponseHeaders(status, ResponseLength.NONE);
        exchange.getResponseBody().close();
    }

    public static void writeCookies(final HttpExchange exchange, final int status, final List<String> cookies) throws IOException {
        cookies.forEach(cookie -> headers(exchange).add("Set-Cookie", cookie));
        write(exchange, status);
    }

    public static void writeLocation(final HttpExchange exchange, final int status, final String location) throws IOException {
        headers(exchange).add("Location", location);
        write(exchange, status);
    }

    public static void writeHtml(final HttpExchange exchange, final int status, final String html) throws IOException {
        headers(exchange).add("Content-Type", "text/html");
        final var bytes = html.getBytes();
        writeBytes(exchange, status, bytes);
    }

    public static void writeJson(
        final HttpExchange exchange,
        final int status,
        final Object object
    ) throws IOException {
        final var string = GsonHolder.INSTANCE.toJson(object);
        writeJson(exchange, status, string);
    }

    public static void writeJson(
        final HttpExchange exchange,
        final int status,
        final String string
    ) throws IOException {
        headers(exchange).add("Content-Type", "application/json");
        final var bytes = string.getBytes();
        writeBytes(exchange, status, bytes);
    }

    public static void writeProblemJson(
        final HttpExchange exchange,
        final int status,
        final Object object
    ) throws IOException {
        headers(exchange).add("Content-Type", "application/problem+json");
        final var string = GsonHolder.INSTANCE.toJson(object);
        final var bytes = string.getBytes();
        writeBytes(exchange, status, bytes);
    }

    public static void writeFile(
        final HttpExchange exchange,
        final int status,
        final File file
    ) throws IOException {
        headers(exchange).add("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        try (final var inputStream = new FileInputStream(file)) {
            writeStream(exchange, status, inputStream, file.length());
        }
    }

    public static void writeStream(
        final HttpExchange exchange,
        final int status,
        final InputStream inputStream,
        final Long length
    ) throws IOException {
        headers(exchange).add("Content-Type", "application/octet-stream");
        exchange.sendResponseHeaders(status, length == null ? ResponseLength.CHUNKED : length);
        try (final var outputStream = exchange.getResponseBody()) {
            StreamService.copy(inputStream, outputStream);
        }
    }

    private static void writeBytes(
        final HttpExchange exchange,
        final int status,
        final byte[] bytes
    ) throws IOException {
        exchange.sendResponseHeaders(status, bytes.length);
        try (final var outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
            outputStream.flush();
        }
    }

    private static Headers headers(final HttpExchange exchange) {
        return exchange.getResponseHeaders();
    }
}
