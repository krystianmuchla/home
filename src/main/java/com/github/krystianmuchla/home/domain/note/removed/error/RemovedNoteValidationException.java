package com.github.krystianmuchla.home.domain.note.removed.error;

import com.github.krystianmuchla.home.domain.core.error.ValidationException;

import java.util.Collection;

public class RemovedNoteValidationException extends ValidationException {
    public final Collection<RemovedNoteValidationError> errors;

    public RemovedNoteValidationException(Collection<RemovedNoteValidationError> errors) {
        this.errors = errors;
    }
}
