package com.github.krystianmuchla.home.domain.drive.file.error;

import com.github.krystianmuchla.home.domain.core.error.DomainException;

import java.util.Collection;

public class FileValidationException extends DomainException {
    public final Collection<FileValidationError> errors;

    public FileValidationException(Collection<FileValidationError> errors) {
        this.errors = errors;
    }
}
