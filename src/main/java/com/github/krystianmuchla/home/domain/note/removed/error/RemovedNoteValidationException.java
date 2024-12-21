package com.github.krystianmuchla.home.domain.note.removed.error;

import java.util.Collection;

public class RemovedNoteValidationException extends Exception {
    public final Collection<RemovedNoteValidationError> errors;

    public RemovedNoteValidationException(Collection<RemovedNoteValidationError> errors) {
        this.errors = errors;
    }
}
