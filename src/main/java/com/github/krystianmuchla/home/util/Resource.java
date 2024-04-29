package com.github.krystianmuchla.home.util;

import com.github.krystianmuchla.home.db.changelog.ChangelogService;
import com.github.krystianmuchla.home.error.exception.InternalException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Resource {
    public static String read(final String fileName) {
        try (final var inputStream = inputStream(fileName)) {
            try (final var outputStream = new ByteArrayOutputStream()) {
                StreamService.copy(inputStream, outputStream);
                return outputStream.toString(StandardCharsets.UTF_8);
            }
        } catch (final IOException exception) {
            throw new InternalException(exception);
        }
    }

    public static InputStream inputStream(final String fileName) {
        return ChangelogService.class.getClassLoader().getResourceAsStream(fileName);
    }
}
