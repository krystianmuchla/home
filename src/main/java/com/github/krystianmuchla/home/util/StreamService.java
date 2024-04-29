package com.github.krystianmuchla.home.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamService {
    public static void copy(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        final var buffer = new byte[512];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
    }
}
