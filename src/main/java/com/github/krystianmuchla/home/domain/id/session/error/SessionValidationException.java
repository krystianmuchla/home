package com.github.krystianmuchla.home.domain.id.session.error;

import java.util.Collection;

public class SessionValidationException extends Exception {
    public final Collection<SessionValidationError> errors;

    public SessionValidationException(Collection<SessionValidationError> errors) {
        this.errors = errors;
    }
}
