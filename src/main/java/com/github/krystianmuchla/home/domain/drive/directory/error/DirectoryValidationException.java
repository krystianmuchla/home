package com.github.krystianmuchla.home.domain.drive.directory.error;

import com.github.krystianmuchla.home.domain.core.error.ValidationException;

import java.util.Collection;

public class DirectoryValidationException extends ValidationException {
    public final Collection<DirectoryValidationError> errors;

    public DirectoryValidationException(Collection<DirectoryValidationError> errors) {
        this.errors = errors;
    }
}
