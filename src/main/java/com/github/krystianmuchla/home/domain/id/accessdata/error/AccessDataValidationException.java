package com.github.krystianmuchla.home.domain.id.accessdata.error;

import java.util.Collection;

public class AccessDataValidationException extends Exception {
    public final Collection<AccessDataValidationError> errors;

    public AccessDataValidationException(Collection<AccessDataValidationError> errors) {
        this.errors = errors;
    }
}
