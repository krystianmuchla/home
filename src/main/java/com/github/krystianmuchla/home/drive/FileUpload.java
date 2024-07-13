package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.exception.InternalException;

import java.io.InputStream;

public record FileUpload(String fileName, InputStream inputStream) {
    public FileUpload {
        if (fileName == null) {
            throw new InternalException("File name cannot be null");
        }
        if (inputStream == null) {
            throw new InternalException("Input stream cannot be null");
        }
    }
}
