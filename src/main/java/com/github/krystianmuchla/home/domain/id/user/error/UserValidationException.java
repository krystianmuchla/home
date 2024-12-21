package com.github.krystianmuchla.home.domain.id.user.error;

import java.util.Collection;

public class UserValidationException extends Exception {
    public final Collection<UserValidationError> errors;

    public UserValidationException(Collection<UserValidationError> errors) {
        this.errors = errors;
    }
}
