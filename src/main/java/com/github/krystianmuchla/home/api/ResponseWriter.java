package com.github.krystianmuchla.home.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.krystianmuchla.home.error.exception.InternalException;
import com.github.krystianmuchla.home.util.StreamService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResponseWriter {
    public static void writeHtml(final HttpServletResponse response, final String html) {
        response.setContentType("text/html");
        writeString(response, html);
    }

    public static void writeJson(final HttpServletResponse response, final Object jsonObject) {
        final var objectMapper = ObjectMapperHolder.INSTANCE;
        final String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(jsonObject);
        } catch (final JsonProcessingException exception) {
            throw new InternalException(exception);
        }
        writeJson(response, jsonString);
    }

    public static void writeJson(final HttpServletResponse response, final String json) {
        response.setContentType("application/json");
        writeString(response, json);
    }

    public static void writeFile(final HttpServletResponse response, final File file) {
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        try (final var inputStream = new FileInputStream(file)) {
            writeStream(response, inputStream);
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
    }

    public static void writeStream(final HttpServletResponse response, final InputStream inputStream) throws IOException {
        response.setContentType("application/octet-stream");
        try (final var outputStream = response.getOutputStream()) {
            StreamService.copy(inputStream, outputStream);
        }
    }

    public static void addCookies(final HttpServletResponse response, final Cookie[] cookies) {
        for (final var cookie : cookies) {
            response.addCookie(cookie);
        }
    }

    private static void writeString(final HttpServletResponse response, final String string) {
        try (final var writer = response.getWriter()) {
            writer.print(string);
            writer.flush();
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
    }
}
