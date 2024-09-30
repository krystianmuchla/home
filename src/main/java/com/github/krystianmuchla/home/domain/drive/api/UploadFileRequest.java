package com.github.krystianmuchla.home.domain.drive.api;

import com.github.krystianmuchla.home.application.exception.ValidationError;
import com.github.krystianmuchla.home.domain.drive.DriveValidator;
import com.github.krystianmuchla.home.infrastructure.http.Header;
import com.github.krystianmuchla.home.infrastructure.http.api.RequestHeaders;
import com.github.krystianmuchla.home.infrastructure.http.exception.BadRequestException;
import com.sun.net.httpserver.Headers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public record UploadFileRequest(String fileName) implements RequestHeaders {
    public UploadFileRequest(Headers headers) {
        this(resolveFileName(headers));
    }

    @Override
    public void validate() {
        var errors = DriveValidator.validateFileName(fileName);
        if (!errors.isEmpty()) {
            throw new BadRequestException(Header.FILE_NAME, errors);
        }
    }

    private static String resolveFileName(Headers headers) {
        var fileName = headers.getFirst(Header.FILE_NAME);
        try {
            return URLDecoder.decode(fileName, StandardCharsets.UTF_8);
        } catch (NullPointerException nullPointerException) {
            throw new BadRequestException(Header.FILE_NAME, ValidationError.nullValue());
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new BadRequestException(Header.FILE_NAME, ValidationError.wrongFormat());
        }
    }
}
