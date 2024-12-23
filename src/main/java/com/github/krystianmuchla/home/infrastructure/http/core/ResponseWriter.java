package com.github.krystianmuchla.home.infrastructure.http.core;

import com.github.krystianmuchla.home.application.util.StreamService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ResponseWriter {
    public static void write(HttpExchange exchange, int status) throws IOException {
        exchange.sendResponseHeaders(status, ResponseLength.NONE);
        exchange.getResponseBody().close();
    }

    public static void writeCookies(HttpExchange exchange, int status, List<String> cookies) throws IOException {
        cookies.forEach(cookie -> headers(exchange).add("Set-Cookie", cookie));
        write(exchange, status);
    }

    public static void writeLocation(HttpExchange exchange, int status, String location) throws IOException {
        headers(exchange).add("Location", location);
        write(exchange, status);
    }

    public static void writeText(HttpExchange exchange, int status, String string) throws IOException {
        writeString(exchange, status, "text/plain", string);
    }

    public static void writeHtml(HttpExchange exchange, int status, Object object) throws IOException {
        var string = object.toString();
        writeHtml(exchange, status, string);
    }

    public static void writeHtml(HttpExchange exchange, int status, String string) throws IOException {
        writeString(exchange, status, "text/html", string);
    }

    public static void writeJson(HttpExchange exchange, int status, Object object) throws IOException {
        var string = GsonHolder.INSTANCE.toJson(object);
        writeJson(exchange, status, string);
    }

    public static void writeJson(HttpExchange exchange, int status, String string) throws IOException {
        writeString(exchange, status, "application/json", string);
    }

    public static void writeProblemJson(HttpExchange exchange, int status, Object object) throws IOException {
        var string = GsonHolder.INSTANCE.toJson(object);
        writeString(exchange, status, "application/problem+json", string);
    }

    public static void writeFile(HttpExchange exchange, int status, String name, File file) throws IOException {
        headers(exchange).add("Content-Disposition", "attachment; filename=\"" + name + "\"");
        try (var inputStream = new FileInputStream(file)) {
            writeStream(exchange, status, inputStream, file.length());
        }
    }

    public static void writeStream(
        HttpExchange exchange,
        int status,
        InputStream inputStream,
        Long length
    ) throws IOException {
        headers(exchange).add("Content-Type", "application/octet-stream");
        exchange.sendResponseHeaders(status, length == null ? ResponseLength.CHUNKED : length);
        try (var outputStream = exchange.getResponseBody()) {
            StreamService.copy(inputStream, outputStream);
        }
    }

    public static void writeString(
        HttpExchange exchange,
        int status,
        String contentType,
        String string
    ) throws IOException {
        var bytes = string.getBytes();
        writeBytes(exchange, status, contentType, bytes);
    }

    public static void writeBytes(
        HttpExchange exchange,
        int status,
        String contentType,
        byte[] bytes
    ) throws IOException {
        headers(exchange).add("Content-Type", contentType);
        writeBytes(exchange, status, bytes);
    }

    private static void writeBytes(HttpExchange exchange, int status, byte[] bytes) throws IOException {
        exchange.sendResponseHeaders(status, bytes.length);
        try (var outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
            outputStream.flush();
        }
    }

    private static Headers headers(HttpExchange exchange) {
        return exchange.getResponseHeaders();
    }
}
