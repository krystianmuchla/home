package com.github.krystianmuchla.home.domain.id.accessdata.error;

import com.github.krystianmuchla.home.domain.core.error.ValidationException;

import java.util.Collection;

public class AccessDataValidationException extends ValidationException {
    public final Collection<AccessDataValidationError> errors;

    public AccessDataValidationException(Collection<AccessDataValidationError> errors) {
        this.errors = errors;
    }
}
