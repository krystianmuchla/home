package com.github.krystianmuchla.home.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.krystianmuchla.home.error.exception.InternalException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ResponseWriter {

    public static void writeJson(final HttpServletResponse response, final Object object) {
        final var objectMapper = ObjectMapperHolder.INSTANCE;
        final String string;
        try {
            string = objectMapper.writeValueAsString(object);
        } catch (final JsonProcessingException exception) {
            throw new InternalException(exception);
        }
        writeJson(response, string);
    }

    public static void writeJson(final HttpServletResponse response, final String string) {
        response.setContentType("application/json");
        try (final var writer = response.getWriter()) {
            writer.print(string);
            writer.flush();
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
    }

    public static void writeFile(final HttpServletResponse response, final File file) {
        response.setContentType("application/octet-stream");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        try (final var outputStream = response.getOutputStream()) {
            try (final var inputStream = new FileInputStream(file)) {
                final var buffer = new byte[512];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
            outputStream.flush();
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
    }

    public static void addCookies(final HttpServletResponse response, final Cookie[] cookies) {
        for (final var cookie : cookies) {
            response.addCookie(cookie);
        }
    }
}
