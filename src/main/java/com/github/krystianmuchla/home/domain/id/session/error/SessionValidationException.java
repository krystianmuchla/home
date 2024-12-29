package com.github.krystianmuchla.home.domain.id.session.error;

import com.github.krystianmuchla.home.domain.core.error.DomainException;

import java.util.Collection;

public class SessionValidationException extends DomainException {
    public final Collection<SessionValidationError> errors;

    public SessionValidationException(Collection<SessionValidationError> errors) {
        this.errors = errors;
    }
}
