package com.github.krystianmuchla.home.domain.id.password.error;

import java.util.Collection;

public class PasswordValidationException extends Exception {
    public final Collection<PasswordValidationError> errors;

    public PasswordValidationException(Collection<PasswordValidationError> errors) {
        this.errors = errors;
    }
}
