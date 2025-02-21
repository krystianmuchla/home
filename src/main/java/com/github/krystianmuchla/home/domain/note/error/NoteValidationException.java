package com.github.krystianmuchla.home.domain.note.error;

import com.github.krystianmuchla.home.domain.core.error.ValidationException;

import java.util.Collection;

public class NoteValidationException extends ValidationException {
    public final Collection<NoteValidationError> errors;

    public NoteValidationException(Collection<NoteValidationError> errors) {
        this.errors = errors;
    }
}
