package com.github.krystianmuchla.home.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import lombok.SneakyThrows;

public class FileAccessor {
    @SneakyThrows
    public static File getFromResources(final String fileName) {
        final var url = FileAccessor.class.getClassLoader().getResource(fileName);
        if (url == null) {
            return null;
        }
        return new File(url.toURI());
    }

    @SneakyThrows
    public static List<String> readFile(final File file, final String separator) {
        final var result = new ArrayList<String>();
        try (final var scanner = new Scanner(file)) {
            scanner.useDelimiter(separator);
            while (scanner.hasNext()) {
                result.add(scanner.next());
            }
        }
        return result;
    }
}
