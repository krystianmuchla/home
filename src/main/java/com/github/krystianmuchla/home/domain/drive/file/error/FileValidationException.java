package com.github.krystianmuchla.home.domain.drive.file.error;

import java.util.Collection;

public class FileValidationException extends Exception {
    public final Collection<FileValidationError> errors;

    public FileValidationException(Collection<FileValidationError> errors) {
        this.errors = errors;
    }
}
