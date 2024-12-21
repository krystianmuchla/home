package com.github.krystianmuchla.home.domain.drive.directory.error;

import java.util.Collection;

public class DirectoryValidationException extends Exception {
    public final Collection<DirectoryValidationError> errors;

    public DirectoryValidationException(Collection<DirectoryValidationError> errors) {
        this.errors = errors;
    }
}
