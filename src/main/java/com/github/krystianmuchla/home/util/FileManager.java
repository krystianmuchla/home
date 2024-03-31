package com.github.krystianmuchla.home.util;

import com.github.krystianmuchla.home.error.exception.InternalException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileManager {
    public static File fromResources(final String fileName) {
        final var url = FileManager.class.getClassLoader().getResource(fileName);
        if (url == null) {
            return null;
        }
        final URI uri;
        try {
            uri = url.toURI();
        } catch (final URISyntaxException exception) {
            throw new InternalException(exception);
        }
        return new File(uri);
    }

    public static List<String> read(final File file, final String separator) throws FileNotFoundException {
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
