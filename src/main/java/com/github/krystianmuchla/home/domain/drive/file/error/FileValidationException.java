package com.github.krystianmuchla.home.domain.drive.file.error;

import com.github.krystianmuchla.home.domain.core.error.ValidationException;

import java.util.Collection;

public class FileValidationException extends ValidationException {
    public final Collection<FileValidationError> errors;

    public FileValidationException(Collection<FileValidationError> errors) {
        this.errors = errors;
    }
}
