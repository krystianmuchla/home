package com.github.krystianmuchla.home.infrastructure.http.core;

import com.github.krystianmuchla.home.application.util.StreamService;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.util.List;

public class ResponseWriter {
    private final HttpExchange exchange;
    private int status = 200;
    private String cacheControl = "no-store, no-cache";
    private ResponseContent<?> content;

    public ResponseWriter(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public ResponseWriter status(int status) {
        this.status = status;
        return this;
    }

    public ResponseWriter header(String name, String value) {
        exchange.getResponseHeaders().add(name, value);
        return this;
    }

    public ResponseWriter cookies(List<String> cookies) {
        cookies.forEach(cookie -> header("Set-Cookie", cookie));
        return this;
    }

    public ResponseWriter location(String location) {
        header("Location", location);
        return this;
    }

    public ResponseWriter contentDisposition(String contentDisposition) {
        header("Content-Disposition", contentDisposition);
        return this;
    }

    public ResponseWriter cacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
        return this;
    }

    public ResponseWriter content(String contentType, String content) {
        content(contentType, content.getBytes());
        return this;
    }

    public ResponseWriter content(String contentType, byte[] content) {
        header("Content-Type", contentType);
        this.content = new BytesResponseContent(content);
        return this;
    }

    public ResponseWriter content(String contentType, Long contentLength, InputStream content) {
        header("Content-Type", contentType);
        this.content = new InputStreamResponseContent(contentLength, content);
        return this;
    }

    public ResponseWriter text(String text) {
        content("text/plain", text);
        return this;
    }

    public ResponseWriter html(String html) {
        content("text/html", html);
        return this;
    }

    public ResponseWriter html(Object html) {
        html(html.toString());
        return this;
    }

    public ResponseWriter json(String json) {
        content("application/json", json);
        return this;
    }

    public ResponseWriter json(Object json) {
        json(GsonHolder.INSTANCE.toJson(json));
        return this;
    }

    public ResponseWriter file(String fileName, File file) throws FileNotFoundException {
        contentDisposition("attachment; filename=\"" + fileName + "\"");
        var contentType = HttpService.resolveContentType(fileName);
        content(contentType, file.length(), new FileInputStream(file));
        return this;
    }

    public void write() throws IOException {
        writeHeaders();
        writeBody();
    }

    private void writeHeaders() throws IOException {
        header("Cache-Control", cacheControl);
        long length;
        if (content == null) {
            length = ResponseLength.NONE;
        } else {
            switch (content) {
                case BytesResponseContent bytes -> length = bytes.value.length;
                case InputStreamResponseContent inputStream -> {
                    if (inputStream.length == null) {
                        length = ResponseLength.CHUNKED;
                    } else {
                        length = inputStream.length;
                    }
                }
            }
        }
        exchange.sendResponseHeaders(status, length);
    }

    private void writeBody() throws IOException {
        if (content == null) {
            exchange.getResponseBody().close();
            return;
        }
        switch (content) {
            case BytesResponseContent bytes -> {
                try (var outputStream = exchange.getResponseBody()) {
                    outputStream.write(bytes.value);
                    outputStream.flush();
                }
            }
            case InputStreamResponseContent inputStream -> {
                try (var outputStream = exchange.getResponseBody()) {
                    StreamService.copy(inputStream.value, outputStream);
                }
                inputStream.value.close();
            }
        }
    }
}
