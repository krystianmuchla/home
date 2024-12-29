package com.github.krystianmuchla.home.domain.drive.directory.error;

import com.github.krystianmuchla.home.domain.core.error.DomainException;

import java.util.Collection;

public class DirectoryValidationException extends DomainException {
    public final Collection<DirectoryValidationError> errors;

    public DirectoryValidationException(Collection<DirectoryValidationError> errors) {
        this.errors = errors;
    }
}
