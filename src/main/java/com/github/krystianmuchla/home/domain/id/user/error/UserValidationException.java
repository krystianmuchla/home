package com.github.krystianmuchla.home.domain.id.user.error;

import com.github.krystianmuchla.home.domain.core.error.ValidationException;

import java.util.Collection;

public class UserValidationException extends ValidationException {
    public final Collection<UserValidationError> errors;

    public UserValidationException(Collection<UserValidationError> errors) {
        this.errors = errors;
    }
}
