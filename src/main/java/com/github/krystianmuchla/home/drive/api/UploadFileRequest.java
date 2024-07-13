package com.github.krystianmuchla.home.drive.api;

import com.github.krystianmuchla.home.api.RequestHeaders;
import com.github.krystianmuchla.home.drive.http.Header;
import com.github.krystianmuchla.home.exception.ValidationError;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.sun.net.httpserver.Headers;

public record UploadFileRequest(String fileName) implements RequestHeaders {
    public UploadFileRequest(final Headers headers) {
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

    private static String resolveFileName(final Headers headers) {
        return headers.getFirst(Header.FILE_NAME);
    }
}
