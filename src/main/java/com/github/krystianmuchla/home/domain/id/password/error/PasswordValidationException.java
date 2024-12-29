package com.github.krystianmuchla.home.domain.id.password.error;

import com.github.krystianmuchla.home.domain.core.error.DomainException;

import java.util.Collection;

public class PasswordValidationException extends DomainException {
    public final Collection<PasswordValidationError> errors;

    public PasswordValidationException(Collection<PasswordValidationError> errors) {
        this.errors = errors;
    }
}
