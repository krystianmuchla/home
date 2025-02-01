package com.github.krystianmuchla.home.infrastructure.http.drive.file;

import com.github.krystianmuchla.home.infrastructure.http.core.error.ValidationError;
import com.github.krystianmuchla.home.infrastructure.http.core.Header;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestHeaders;
import com.github.krystianmuchla.home.infrastructure.http.core.error.BadRequestException;
import com.sun.net.httpserver.Headers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public record UploadFileRequest(String fileName) implements RequestHeaders {
    public UploadFileRequest(Headers headers) {
        this(resolveFileName(headers));
    }

    private static String resolveFileName(Headers headers) {
        var fileName = headers.getFirst(Header.FILE_NAME);
        if (fileName == null) {
            return null;
        }
        try {
            return URLDecoder.decode(fileName, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException(Header.FILE_NAME, ValidationError.wrongFormat());
        }
    }
}
