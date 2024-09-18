package com.github.krystianmuchla.home.drive.api;

import com.github.krystianmuchla.home.api.RequestHeaders;
import com.github.krystianmuchla.home.drive.http.Header;
import com.github.krystianmuchla.home.exception.ValidationError;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.sun.net.httpserver.Headers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public record UploadFileRequest(String fileName) implements RequestHeaders {
    public UploadFileRequest(Headers headers) {
        this(resolveFileName(headers));
    }

    @Override
    public void validate() {
        if (fileName == null) {
            throw new BadRequestException(Header.FILE_NAME, ValidationError.nullValue());
        }
        if (fileName.isBlank()) {
            throw new BadRequestException(Header.FILE_NAME, ValidationError.emptyValue());
        }
    }

    private static String resolveFileName(Headers headers) {
        var fileName = headers.getFirst(Header.FILE_NAME);
        return URLDecoder.decode(fileName, StandardCharsets.UTF_8);
    }
}
