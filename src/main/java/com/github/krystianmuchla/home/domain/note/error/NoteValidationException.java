package com.github.krystianmuchla.home.domain.note.error;

import java.util.Collection;

public class NoteValidationException extends Exception {
    public final Collection<NoteValidationError> errors;

    public NoteValidationException(Collection<NoteValidationError> errors) {
        this.errors = errors;
    }
}
