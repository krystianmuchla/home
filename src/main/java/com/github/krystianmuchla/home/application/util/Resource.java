package com.github.krystianmuchla.home.application.util;

import com.github.krystianmuchla.home.application.exception.InternalException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Resource {
    public static String read(String fileName) {
        try (var inputStream = inputStream(fileName)) {
            try (var outputStream = new ByteArrayOutputStream()) {
                StreamService.copy(inputStream, outputStream);
                return outputStream.toString(StandardCharsets.UTF_8);
            }
        } catch (IOException exception) {
            throw new InternalException(exception);
        }
    }

    public static InputStream inputStream(String fileName) {
        return Resource.class.getClassLoader().getResourceAsStream(fileName);
    }
}
