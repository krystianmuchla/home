package com.github.krystianmuchla.home.domain.note.removed.error;

import com.github.krystianmuchla.home.domain.core.error.DomainException;

import java.util.Collection;

public class RemovedNoteValidationException extends DomainException {
    public final Collection<RemovedNoteValidationError> errors;

    public RemovedNoteValidationException(Collection<RemovedNoteValidationError> errors) {
        this.errors = errors;
    }
}
